#!/usr/bin/env python3
"""Publish a sample cloud_metrics event to Kafka for pipeline testing."""

import json
import sys
import uuid

from kafka import KafkaProducer
from kafka.errors import NoBrokersAvailable

from config import INPUT_TOPIC, KAFKA_BOOTSTRAP
from label_generator import generate_cloud_metric_sample


def main():
    import random

    metric = generate_cloud_metric_sample(random.Random())
    metric["eventId"] = str(uuid.uuid4())

    try:
        producer = KafkaProducer(
            bootstrap_servers=KAFKA_BOOTSTRAP.split(","),
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
        )
    except NoBrokersAvailable as exc:
        print(f"Kafka unavailable: {exc}", file=sys.stderr)
        return 1

    producer.send(INPUT_TOPIC, value=metric)
    producer.flush()
    producer.close()

    print(f"Published to '{INPUT_TOPIC}':")
    print(json.dumps(metric, indent=2))
    return 0


if __name__ == "__main__":
    sys.exit(main())
