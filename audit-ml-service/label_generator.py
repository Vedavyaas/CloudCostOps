"""Synthetic label generation for training the audit metric predictor.

Labels mirror the formulas documented in OrchestrationEngine (KafkaController
comments and CloudAuditMetricEntity field descriptions).
"""

from __future__ import annotations

import random
from datetime import datetime, timezone
from typing import Any

from features import cloud_metric_to_features

OUTPUT_FIELDS: list[str] = [
    "totalEstimatedCost",
    "computeCostPercentage",
    "memoryCostPercentage",
    "networkCostPercentage",
    "storageCostPercentage",
    "costPerRequest",
    "costPerQuery",
    "dailyCost",
    "weeklyCost",
    "monthlyCost",
    "costGrowthRate",
    "resourceUtilizationScore",
    "averageCpuUsage",
    "peakCpuUsage",
    "averageMemoryUsage",
    "peakMemoryUsage",
    "averageDiskUsage",
    "averageNetworkUsage",
    "connectionUtilization",
    "queryEfficiencyScore",
    "averageQPS",
    "peakQPS",
    "databaseGrowthRate",
    "cacheEfficiencyScore",
    "availabilityScore",
    "errorTrend",
    "requestGrowthRate",
    "averageResponseTime",
    "peakResponseTime",
    "applicationHealthScore",
    "daysUntilStorageFull",
    "projectedMonthlyTraffic",
    "projectedMonthlyCost",
    "cpuGrowthRate",
    "memoryGrowthRate",
    "cpuAnomalyScore",
    "memoryAnomalyScore",
    "networkAnomalyScore",
    "databaseAnomalyScore",
    "responseTimeAnomalyScore",
    "overallAnomalyScore",
]

RESOURCE_TYPES = ["POSTGRESQL", "MYSQL", "MONGODB", "EC2", "LAMBDA"]
ENVIRONMENTS = ["PRODUCTION", "STAGING", "DEVELOPMENT"]
REGIONS = ["ap-south-1", "us-east-1", "eu-west-1"]
AGENT_IDS = ["prod-agent-1", "prod-agent-2", "dev-agent-1", "staging-agent"]
COMPANIES = ["amazon", "vercel", "acme", "globex"]


def _clamp(value: float, low: float, high: float) -> float:
    return max(low, min(high, value))


def _anomaly_score(value: float, baseline: float, spread: float) -> float:
    deviation = abs(value - baseline) / max(spread, 1e-6)
    return _clamp(deviation / 5.0, 0.0, 1.0)


def generate_cloud_metric_sample(rng: random.Random) -> dict[str, Any]:
    cpu = 5.0 + rng.random() * 90.0
    mem_pct = 15.0 + rng.random() * 80.0
    total_mb = 16384 + rng.randint(0, 3) * 8192
    used_mb = int(total_mb * mem_pct / 100.0)
    disk_pct = 10.0 + rng.random() * 75.0
    storage_gb = 20.0 + rng.random() * 980.0
    net_in = 0.5 + rng.random() * 250.0
    net_out = 0.5 + rng.random() * 200.0
    qps = 5 + rng.randint(0, 995)
    query_ms = 0.5 + rng.random() * 199.0
    cache_hit = 0.5 + rng.random() * 0.49
    db_conns = 1 + rng.randint(0, 499)
    rpm = 10 + rng.randint(0, 4990)
    err_pct = rng.random() * 8.0
    resp_ms = 10.0 + rng.random() * 990.0

    company = rng.choice(COMPANIES)
    agent_id = rng.choice(AGENT_IDS)

    return {
        "eventId": f"evt-{rng.randint(1, 10_000_000)}",
        "company": {"companyName": company},
        "agent": {
            "agentId": agent_id,
            "hostname": f"{agent_id}.internal",
            "ipAddress": f"10.0.{rng.randint(0, 255)}.{rng.randint(1, 254)}",
        },
        "resource": {
            "resourceType": rng.choice(RESOURCE_TYPES),
            "resourceId": f"res-{rng.randint(1000, 9999)}",
            "environment": rng.choice(ENVIRONMENTS),
            "region": rng.choice(REGIONS),
            "availabilityZone": f"{rng.choice(REGIONS)}a",
        },
        "compute": {
            "cpuUsagePercent": round(cpu, 2),
            "cpuCores": 4 + rng.randint(0, 28),
            "loadAverage1m": round(cpu / 10.0, 2),
            "loadAverage5m": round(cpu / 12.0, 2),
            "loadAverage15m": round(cpu / 15.0, 2),
        },
        "memory": {
            "totalMB": total_mb,
            "usedMB": used_mb,
            "freeMB": total_mb - used_mb,
            "usagePercent": round(mem_pct, 2),
        },
        "network": {
            "networkInMB": round(net_in, 2),
            "networkOutMB": round(net_out, 2),
            "activeConnections": 5 + rng.randint(0, 1995),
        },
        "disk": {
            "diskReadMB": round(5.0 + rng.random() * 300.0, 2),
            "diskWriteMB": round(2.0 + rng.random() * 200.0, 2),
            "diskUsagePercent": round(disk_pct, 2),
            "storageUsedGB": round(storage_gb, 2),
        },
        "database": {
            "activeConnections": db_conns,
            "queriesPerSecond": qps,
            "averageQueryTimeMs": round(query_ms, 2),
            "cacheHitRatio": round(cache_hit, 4),
            "databaseSizeGB": round(0.5 + rng.random() * 199.0, 2),
        },
        "application": {
            "requestsPerMinute": rpm,
            "errorRatePercent": round(err_pct, 3),
            "responseTimeMs": round(resp_ms, 2),
        },
    }


def derive_audit_labels(metric: dict[str, Any], rng: random.Random | None = None) -> dict[str, Any]:
    """Compute ground-truth audit metrics from a cloud_metrics payload."""
    rng = rng or random.Random()

    compute = metric.get("compute") or {}
    memory = metric.get("memory") or {}
    network = metric.get("network") or {}
    disk = metric.get("disk") or {}
    database = metric.get("database") or {}
    application = metric.get("application") or {}

    cpu = float(compute.get("cpuUsagePercent", 0.0))
    mem_pct = float(memory.get("usagePercent", 0.0))
    disk_pct = float(disk.get("diskUsagePercent", 0.0))
    net_in = float(network.get("networkInMB", 0.0))
    net_out = float(network.get("networkOutMB", 0.0))
    storage_gb = float(disk.get("storageUsedGB", 0.0))
    qps = float(database.get("queriesPerSecond", 0.0))
    query_ms = max(float(database.get("averageQueryTimeMs", 1.0)), 0.1)
    cache_hit = float(database.get("cacheHitRatio", 0.0))
    db_conns = float(database.get("activeConnections", 0.0))
    rpm = float(application.get("requestsPerMinute", 0.0))
    err_pct = float(application.get("errorRatePercent", 0.0))
    resp_ms = float(application.get("responseTimeMs", 0.0))
    cpu_cores = float(compute.get("cpuCores", 1.0))

    # Utilization (documented formulas)
    avg_cpu = cpu
    peak_cpu = _clamp(cpu * (1.1 + rng.random() * 0.25), cpu, 100.0)
    avg_mem = mem_pct
    peak_mem = _clamp(mem_pct * (1.05 + rng.random() * 0.2), mem_pct, 100.0)
    avg_disk = disk_pct
    avg_net = net_in
    resource_util = (avg_cpu + avg_mem + avg_disk) / 3.0

    query_eff = qps / query_ms
    cache_eff = cache_hit * 100.0
    conn_util = _clamp(db_conns / 100.0, 0.0, 1.0)

    error_penalty = err_pct * 2.5
    response_penalty = _clamp(resp_ms / 50.0, 0.0, 40.0)
    app_health = _clamp(100.0 - error_penalty - response_penalty, 0.0, 100.0)
    availability = _clamp(100.0 - err_pct * 3.0, 85.0, 100.0)

    # Cost model (weighted resource usage)
    compute_cost = cpu * 0.85 + cpu_cores * 1.2
    memory_cost = mem_pct * 0.55 + float(memory.get("usedMB", 0.0)) / 1024.0 * 0.15
    network_cost = (net_in + net_out) * 0.35
    storage_cost = disk_pct * 0.4 + storage_gb * 0.08

    total_component = compute_cost + memory_cost + network_cost + storage_cost + 1e-6
    compute_pct = compute_cost / total_component * 100.0
    memory_pct = memory_cost / total_component * 100.0
    network_pct = network_cost / total_component * 100.0
    storage_pct = storage_cost / total_component * 100.0

    total_estimated = 50.0 + compute_cost * 4.5 + memory_cost * 3.0 + network_cost * 2.0 + storage_cost * 2.5
    total_estimated += rng.uniform(-5.0, 5.0)

    daily = total_estimated / 30.0
    weekly = daily * 7.0
    monthly = daily * 30.0
    requests_day = max(rpm * 60.0 * 24.0, 1.0)
    queries_day = max(qps * 86400.0, 1.0)
    cost_per_request = total_estimated / requests_day
    cost_per_query = total_estimated / queries_day

    cost_growth = _clamp((cpu + mem_pct) / 200.0 - 0.2 + rng.uniform(-0.05, 0.05), -0.5, 2.5)
    req_growth = _clamp(rpm / 5000.0 + rng.uniform(-0.2, 0.4), -0.5, 3.0)
    db_growth = _clamp(storage_gb / 1000.0 + rng.uniform(-0.1, 0.2), 0.0, 2.0)
    cpu_growth = _clamp(cpu / 100.0 * 0.8 + rng.uniform(-0.1, 0.1), 0.0, 1.5)
    mem_growth = _clamp(mem_pct / 100.0 * 0.6 + rng.uniform(-0.1, 0.1), 0.0, 1.2)

    days_until_full = int(_clamp((100.0 - disk_pct) / max(disk_pct / 90.0, 0.5), 5.0, 365.0))
    projected_traffic = rpm * 60.0 * 24.0 * 30.0
    projected_cost = monthly * (1.0 + cost_growth * 0.1)

    cpu_anomaly = _anomaly_score(cpu, 45.0, 25.0)
    mem_anomaly = _anomaly_score(mem_pct, 50.0, 25.0)
    net_anomaly = _anomaly_score(net_in + net_out, 200.0, 150.0)
    db_anomaly = _anomaly_score(qps / query_ms, 20.0, 15.0)
    resp_anomaly = _anomaly_score(resp_ms, 150.0, 120.0)
    overall_anomaly = (
        cpu_anomaly + mem_anomaly + net_anomaly + db_anomaly + resp_anomaly
    ) / 5.0

    return {
        "totalEstimatedCost": round(total_estimated, 2),
        "computeCostPercentage": round(compute_pct, 2),
        "memoryCostPercentage": round(memory_pct, 2),
        "networkCostPercentage": round(network_pct, 2),
        "storageCostPercentage": round(storage_pct, 2),
        "costPerRequest": round(cost_per_request, 6),
        "costPerQuery": round(cost_per_query, 6),
        "dailyCost": round(daily, 2),
        "weeklyCost": round(weekly, 2),
        "monthlyCost": round(monthly, 2),
        "costGrowthRate": round(cost_growth, 3),
        "resourceUtilizationScore": round(resource_util, 2),
        "averageCpuUsage": round(avg_cpu, 2),
        "peakCpuUsage": round(peak_cpu, 2),
        "averageMemoryUsage": round(avg_mem, 2),
        "peakMemoryUsage": round(peak_mem, 2),
        "averageDiskUsage": round(avg_disk, 2),
        "averageNetworkUsage": round(avg_net, 2),
        "connectionUtilization": round(conn_util, 3),
        "queryEfficiencyScore": round(query_eff, 2),
        "averageQPS": round(qps, 2),
        "peakQPS": round(qps * (1.2 + rng.random() * 0.5), 2),
        "databaseGrowthRate": round(db_growth, 3),
        "cacheEfficiencyScore": round(cache_eff, 2),
        "availabilityScore": round(availability, 2),
        "errorTrend": round(err_pct, 3),
        "requestGrowthRate": round(req_growth, 3),
        "averageResponseTime": round(resp_ms, 2),
        "peakResponseTime": round(resp_ms * (1.3 + rng.random() * 0.8), 2),
        "applicationHealthScore": round(app_health, 2),
        "daysUntilStorageFull": days_until_full,
        "projectedMonthlyTraffic": round(projected_traffic, 2),
        "projectedMonthlyCost": round(projected_cost, 2),
        "cpuGrowthRate": round(cpu_growth, 3),
        "memoryGrowthRate": round(mem_growth, 3),
        "cpuAnomalyScore": round(cpu_anomaly, 3),
        "memoryAnomalyScore": round(mem_anomaly, 3),
        "networkAnomalyScore": round(net_anomaly, 3),
        "databaseAnomalyScore": round(db_anomaly, 3),
        "responseTimeAnomalyScore": round(resp_anomaly, 3),
        "overallAnomalyScore": round(overall_anomaly, 3),
    }


def build_training_dataset(sample_count: int, seed: int = 42) -> tuple[list[list[float]], list[list[float]]]:
    rng = random.Random(seed)
    features: list[list[float]] = []
    labels: list[list[float]] = []

    for _ in range(sample_count):
        metric = generate_cloud_metric_sample(rng)
        audit = derive_audit_labels(metric, rng)
        features.append(cloud_metric_to_features(metric))
        labels.append([float(audit[field]) for field in OUTPUT_FIELDS])

    return features, labels


def audit_dict_to_kafka_payload(event_id: str, audit: dict[str, Any]) -> dict[str, Any]:
    payload = {"eventId": event_id, "auditTimestamp": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z")}
    payload.update(audit)
    return payload
