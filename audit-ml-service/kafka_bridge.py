"""Kafka bridge: consume cloud_metrics, predict audit metrics, publish cloud_audit_metric."""

from __future__ import annotations

import json
import logging
import signal
import sys
import time
from typing import Any

from kafka import KafkaConsumer, KafkaProducer

from config import (
    CONSUMER_GROUP,
    INPUT_TOPIC,
    KAFKA_BOOTSTRAP,
    OUTPUT_TOPIC,
    PUBLISH_DELAY_SECONDS,
)
from predictor import build_audit_event

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s - %(message)s",
)
log = logging.getLogger("audit-ml-kafka")

_running = True


def _shutdown_handler(_signum, _frame):
    global _running
    log.info("Shutdown signal received, stopping consumer...")
    _running = False


def _create_consumer() -> KafkaConsumer:
    return KafkaConsumer(
        INPUT_TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP.split(","),
        group_id=CONSUMER_GROUP,
        auto_offset_reset="earliest",
        enable_auto_commit=True,
        value_deserializer=lambda m: json.loads(m.decode("utf-8")),
        consumer_timeout_ms=1000,
    )


def _create_producer() -> KafkaProducer:
    return KafkaProducer(
        bootstrap_servers=KAFKA_BOOTSTRAP.split(","),
        value_serializer=lambda v: json.dumps(v).encode("utf-8"),
        acks="all",
        retries=3,
    )


def process_message(payload: dict[str, Any]) -> dict[str, Any]:
    if not payload.get("eventId"):
        raise ValueError("Missing eventId in cloud_metrics payload")
    return build_audit_event(payload)


def run_bridge() -> None:
    signal.signal(signal.SIGINT, _shutdown_handler)
    signal.signal(signal.SIGTERM, _shutdown_handler)

    log.info("Connecting to Kafka at %s", KAFKA_BOOTSTRAP)
    try:
        consumer = _create_consumer()
        producer = _create_producer()
    except Exception as exc:
        log.error("Kafka broker unavailable: %s", exc)
        sys.exit(1)

    log.info(
        "Listening on '%s' → publishing to '%s' (delay=%.1fs)",
        INPUT_TOPIC,
        OUTPUT_TOPIC,
        PUBLISH_DELAY_SECONDS,
    )

    processed = 0
    while _running:
        try:
            for message in consumer:
                if not _running:
                    break

                payload = message.value
                event_id = payload.get("eventId", "unknown")

                try:
                    audit_event = process_message(payload)
                    if PUBLISH_DELAY_SECONDS > 0:
                        time.sleep(PUBLISH_DELAY_SECONDS)

                    producer.send(OUTPUT_TOPIC, value=audit_event)
                    producer.flush()
                    processed += 1
                    log.info(
                        "Processed eventId=%s | total=%d | monthlyCost=%.2f",
                        event_id,
                        processed,
                        audit_event.get("monthlyCost", 0.0),
                    )
                except Exception:
                    log.exception("Failed to process eventId=%s", event_id)

        except Exception:
            if _running:
                log.exception("Consumer loop error, retrying in 3s...")
                time.sleep(3)

    consumer.close()
    producer.close()
    log.info("Stopped. Total events processed: %d", processed)


if __name__ == "__main__":
    run_bridge()
