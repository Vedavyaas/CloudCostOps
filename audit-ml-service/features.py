"""Feature extraction from cloud_metrics Kafka payloads."""

from __future__ import annotations

from typing import Any

FEATURE_NAMES: list[str] = [
    "cpu_usage_percent",
    "cpu_cores",
    "load_avg_1m",
    "load_avg_5m",
    "load_avg_15m",
    "mem_total_mb",
    "mem_used_mb",
    "mem_free_mb",
    "mem_usage_percent",
    "net_in_mb",
    "net_out_mb",
    "net_active_connections",
    "disk_read_mb",
    "disk_write_mb",
    "disk_usage_percent",
    "storage_used_gb",
    "db_active_connections",
    "db_queries_per_second",
    "db_avg_query_time_ms",
    "db_cache_hit_ratio",
    "db_size_gb",
    "app_requests_per_minute",
    "app_error_rate_percent",
    "app_response_time_ms",
    "resource_type_encoded",
    "environment_encoded",
]


def _nested(payload: dict[str, Any], key: str) -> dict[str, Any]:
    value = payload.get(key)
    return value if isinstance(value, dict) else {}


def _num(container: dict[str, Any], key: str, default: float = 0.0) -> float:
    value = container.get(key, default)
    try:
        return float(value)
    except (TypeError, ValueError):
        return default


def _encode_label(value: str | None, mapping: dict[str, float]) -> float:
    if not value:
        return 0.0
    return mapping.get(value.upper(), 0.0)


RESOURCE_TYPE_MAP = {
    "POSTGRESQL": 1.0,
    "MYSQL": 2.0,
    "MONGODB": 3.0,
    "REDIS": 4.0,
    "KUBERNETES": 5.0,
    "EC2": 6.0,
    "LAMBDA": 7.0,
}

ENVIRONMENT_MAP = {
    "PRODUCTION": 1.0,
    "STAGING": 2.0,
    "DEVELOPMENT": 3.0,
    "TEST": 4.0,
}


def cloud_metric_to_features(payload: dict[str, Any]) -> list[float]:
    compute = _nested(payload, "compute")
    memory = _nested(payload, "memory")
    network = _nested(payload, "network")
    disk = _nested(payload, "disk")
    database = _nested(payload, "database")
    application = _nested(payload, "application")
    resource = _nested(payload, "resource")

    return [
        _num(compute, "cpuUsagePercent"),
        _num(compute, "cpuCores"),
        _num(compute, "loadAverage1m"),
        _num(compute, "loadAverage5m"),
        _num(compute, "loadAverage15m"),
        _num(memory, "totalMB"),
        _num(memory, "usedMB"),
        _num(memory, "freeMB"),
        _num(memory, "usagePercent"),
        _num(network, "networkInMB"),
        _num(network, "networkOutMB"),
        _num(network, "activeConnections"),
        _num(disk, "diskReadMB"),
        _num(disk, "diskWriteMB"),
        _num(disk, "diskUsagePercent"),
        _num(disk, "storageUsedGB"),
        _num(database, "activeConnections"),
        _num(database, "queriesPerSecond"),
        _num(database, "averageQueryTimeMs"),
        _num(database, "cacheHitRatio"),
        _num(database, "databaseSizeGB"),
        _num(application, "requestsPerMinute"),
        _num(application, "errorRatePercent"),
        _num(application, "responseTimeMs"),
        _encode_label(resource.get("resourceType"), RESOURCE_TYPE_MAP),
        _encode_label(resource.get("environment"), ENVIRONMENT_MAP),
    ]
