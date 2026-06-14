"""
Static seed data that mirrors DataSeeder.java exactly.
2 companies × 3 months (April/May/June 2026) × 40 records = 240 events.

Publishes to the cloud_metrics Kafka topic so the audit-ml-service (serve mode)
picks each event up, runs ML prediction, and publishes to cloud_audit_metric.
"""

from __future__ import annotations

import json
import logging
import math
import sys
import time
from datetime import datetime, timezone, timedelta
from calendar import monthrange

from kafka import KafkaProducer

from config import INPUT_TOPIC, KAFKA_BOOTSTRAP, PUBLISH_DELAY_SECONDS

log = logging.getLogger("audit-ml-seed")

# ── Agent profiles — mirrors DataSeeder.java constants ─────────────────────
AGENT_IDS  = ["prod-agent-1", "prod-agent-2", "dev-agent-1", "staging-agent"]
BASE_CPU   = [45.0,  62.0,  18.0,  32.0]
BASE_MEM   = [62.0,  74.0,  30.0,  46.0]
CPU_CORES  = [16,    32,     8,    16]
TOTAL_MB   = [32768, 65536, 16384, 32768]
BASE_RPM   = [1800,  3200,   200,   850]
BASE_QPS   = [ 420,   650,    80,   270]
STORAGE_GB = [ 380,   480,    80,   200]


def _clamp(v: float, lo: float, hi: float) -> float:
    return max(lo, min(hi, v))


def _r2(v: float) -> float: return round(v, 2)
def _r3(v: float) -> float: return round(v, 3)
def _r4(v: float) -> float: return round(v, 4)


def _ts(year: int, month: int, day: int, hour: int, minute: int) -> str:
    dt = datetime(year, month, day, hour, minute, 0, tzinfo=timezone.utc)
    return dt.isoformat().replace("+00:00", "Z")


def _generate_month(
    company: str, prefix: str, ip_net: str,
    year: int, month: int, count: int = 40,
) -> list[dict]:
    """
    Generates `count` deterministic cloud_metrics events for one company-month.
    Uses identical sine/cosine formula as DataSeeder.java so eventIds and values match.
    """
    days_in_month = monthrange(year, month)[1]
    agent_ips = [
        f"{ip_net}.0.10",
        f"{ip_net}.0.11",
        f"{ip_net}.0.20",
        f"{ip_net}.0.30",
    ]

    events: list[dict] = []
    for i in range(count):
        agent_idx = i % 4
        agent_id  = AGENT_IDS[agent_idx]
        ip        = agent_ips[agent_idx]

        # ── Timestamp: spread evenly across month (mirrors Java logic) ────
        day_of_month = 1 + (i * days_in_month // count)
        hour         = (i * 23 // count)
        minute       = (i * 7) % 60
        day_of_month = min(day_of_month, days_in_month)

        # ── Deterministic oscillation (same as Java) ──────────────────────
        wave  = math.sin(i * math.pi / 10.0)   # -1..+1
        wave2 = math.cos(i * math.pi / 8.0)    # secondary

        cpu     = _clamp(BASE_CPU[agent_idx] + 15.0 * wave,  5.0, 95.0)
        mem_pct = _clamp(BASE_MEM[agent_idx] + 10.0 * wave2, 10.0, 95.0)

        total_mb = TOTAL_MB[agent_idx]
        used_mb  = int(total_mb * mem_pct / 100.0)
        free_mb  = total_mb - used_mb

        net_in  = _clamp(50.0 + 120.0 * ((cpu - 5.0) / 90.0), 10.0, 350.0)
        net_out = net_in * 0.78
        disk_pct    = _clamp(28.0 + 22.0 * wave,  8.0, 90.0)
        storage_gb  = STORAGE_GB[agent_idx] + month * 8.0 + i * 1.5
        disk_read   = _clamp(40.0 + 90.0 * ((cpu - 5.0) / 90.0), 5.0, 400.0)
        disk_write  = disk_read * 0.62

        qps      = int(_clamp(BASE_QPS[agent_idx] * (0.75 + 0.5 * ((cpu - 5.0) / 90.0)), 5, 2000))
        query_ms = _clamp(4.0 + 22.0 * (1.0 - (cpu - 5.0) / 90.0), 0.5, 200.0)
        cache_hit = _clamp(0.95 - 0.06 * wave, 0.82, 0.999)
        db_conns  = max(1, qps // 8)
        db_size_gb = STORAGE_GB[agent_idx] * 0.4 + month * 3.0 + i * 0.8

        rpm     = int(_clamp(BASE_RPM[agent_idx] * (0.75 + 0.5 * ((cpu - 5.0) / 90.0)), 10, 8000))
        err_pct = _clamp(0.4 + 2.5 * max(0.0, wave), 0.05, 8.0)
        resp_ms = _clamp(40.0 + 220.0 * (1.0 - (cpu - 5.0) / 90.0), 15.0, 900.0)

        event_id = f"evt-{prefix}-{year}-{month:02d}-{i+1:02d}"

        events.append({
            "eventId": event_id,
            "timestamp": _ts(year, month, day_of_month, hour, minute),
            "company": {"companyName": company},
            "agent": {
                "agentId": agent_id,
                "hostname": f"{agent_id}.internal",
                "ipAddress": ip,
            },
            "resource": {
                "resourceType": "EC2",
                "resourceId": f"res-{event_id}",
                "environment": "PRODUCTION",
                "region": "ap-south-1",
                "availabilityZone": "ap-south-1a",
            },
            "compute": {
                "cpuUsagePercent": _r2(cpu),
                "cpuCores": CPU_CORES[agent_idx],
                "loadAverage1m":  _r2(cpu / 10.0),
                "loadAverage5m":  _r2(cpu / 12.0),
                "loadAverage15m": _r2(cpu / 15.0),
            },
            "memory": {
                "totalMB":      total_mb,
                "usedMB":       used_mb,
                "freeMB":       free_mb,
                "usagePercent": _r2(mem_pct),
            },
            "network": {
                "networkInMB":       _r2(net_in),
                "networkOutMB":      _r2(net_out),
                "activeConnections": int(net_in * 0.4),
            },
            "disk": {
                "diskReadMB":       _r2(disk_read),
                "diskWriteMB":      _r2(disk_write),
                "diskUsagePercent": _r2(disk_pct),
                "storageUsedGB":    _r2(storage_gb),
            },
            "database": {
                "activeConnections": db_conns,
                "queriesPerSecond":  qps,
                "averageQueryTimeMs": _r2(query_ms),
                "cacheHitRatio":     _r4(cache_hit),
                "databaseSizeGB":    _r2(db_size_gb),
            },
            "application": {
                "requestsPerMinute": rpm,
                "errorRatePercent":  _r3(err_pct),
                "responseTimeMs":    _r2(resp_ms),
            },
        })

    return events


def _build_all_events() -> list[dict]:
    """Build the full 240-event static dataset."""
    companies = [("amazon", "amz", "10.0"), ("vercel", "vcl", "10.1")]
    year_months = [(2026, 4), (2026, 5), (2026, 6)]
    events: list[dict] = []
    for company, prefix, ip_net in companies:
        for year, month in year_months:
            events.extend(_generate_month(company, prefix, ip_net, year, month))
    return events


# Pre-built at import time so it can be reused without regeneration
STATIC_EVENTS: list[dict] = _build_all_events()


def seed_to_kafka(delay: float = PUBLISH_DELAY_SECONDS) -> int:
    """
    Publish all 240 static cloud_metrics events to Kafka.
    The audit-ml-service (serve mode) will consume each event, run ML prediction,
    and publish the corresponding audit metric to cloud_audit_metric.

    Returns the number of events successfully published.
    """
    log.info("Connecting to Kafka at %s", KAFKA_BOOTSTRAP)
    try:
        producer = KafkaProducer(
            bootstrap_servers=KAFKA_BOOTSTRAP.split(","),
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
            acks="all",
            retries=3,
        )
    except Exception as exc:
        log.error("Kafka broker unavailable: %s", exc)
        return 0

    total = len(STATIC_EVENTS)
    published = 0
    for event in STATIC_EVENTS:
        producer.send(INPUT_TOPIC, value=event)
        producer.flush()
        published += 1
        log.info(
            "[%d/%d] eventId=%-22s  company=%-8s  agent=%s",
            published, total,
            event["eventId"],
            event["company"]["companyName"],
            event["agent"]["agentId"],
        )
        if delay > 0:
            time.sleep(delay)

    producer.close()
    log.info(
        "Seed complete — %d/%d events published to '%s'",
        published, total, INPUT_TOPIC,
    )
    return published


if __name__ == "__main__":
    logging.basicConfig(
        level=logging.INFO,
        format="%(asctime)s [%(levelname)s] %(name)s - %(message)s",
    )
    count = seed_to_kafka()
    sys.exit(0 if count == len(STATIC_EVENTS) else 1)
