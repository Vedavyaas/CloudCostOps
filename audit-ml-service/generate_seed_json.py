"""
Generates the canonical seed_records.json — the single source of truth for all
240 static cloud metric records (2 companies × 3 months × 40 records).

Run once:  python generate_seed_json.py
Output:    ../seed_records.json          (project root — read by Python)
           ../OrchestrationEngine/src/main/resources/seed_records.json  (classpath — read by Java)
"""

from __future__ import annotations

import json
import math
import shutil
from calendar import monthrange
from datetime import datetime, timezone
from pathlib import Path

# ── Agent profiles ─────────────────────────────────────────────────────────────
AGENT_IDS  = ["prod-agent-1", "prod-agent-2", "dev-agent-1", "staging-agent"]
BASE_CPU   = [45.0,  62.0,  18.0,  32.0]
BASE_MEM   = [62.0,  74.0,  30.0,  46.0]
CPU_CORES  = [16,    32,     8,    16]
TOTAL_MB   = [32768, 65536, 16384, 32768]
BASE_RPM   = [1800,  3200,   200,   850]
BASE_QPS   = [ 420,   650,    80,   270]
STORAGE_GB = [ 380.0, 480.0,  80.0, 200.0]


def _clamp(v, lo, hi): return max(lo, min(hi, v))
def _r2(v): return round(v, 2)
def _r3(v): return round(v, 3)
def _r4(v): return round(v, 4)


def _ts(year, month, day, hour, minute):
    return datetime(year, month, day, hour, minute, 0, tzinfo=timezone.utc)\
               .isoformat().replace("+00:00", "Z")


def generate_month(company, prefix, ip_net, year, month, count=40):
    days_in_month = monthrange(year, month)[1]
    agent_ips = [f"{ip_net}.0.10", f"{ip_net}.0.11", f"{ip_net}.0.20", f"{ip_net}.0.30"]
    records = []

    for i in range(count):
        idx = i % 4
        wave  = math.sin(i * math.pi / 10.0)
        wave2 = math.cos(i * math.pi / 8.0)

        cpu     = _clamp(BASE_CPU[idx]  + 15.0 * wave,  5.0, 95.0)
        mem_pct = _clamp(BASE_MEM[idx]  + 10.0 * wave2, 10.0, 95.0)

        total_mb = TOTAL_MB[idx]
        used_mb  = int(total_mb * mem_pct / 100.0)
        free_mb  = total_mb - used_mb

        net_in   = _clamp(50.0 + 120.0 * ((cpu - 5.0) / 90.0), 10.0, 350.0)
        net_out  = net_in * 0.78
        disk_pct = _clamp(28.0 + 22.0 * wave, 8.0, 90.0)
        storage  = STORAGE_GB[idx] + month * 8.0 + i * 1.5
        disk_r   = _clamp(40.0 + 90.0 * ((cpu - 5.0) / 90.0), 5.0, 400.0)
        disk_w   = disk_r * 0.62

        qps      = int(_clamp(BASE_QPS[idx] * (0.75 + 0.5 * ((cpu - 5.0) / 90.0)), 5, 2000))
        query_ms = _clamp(4.0 + 22.0 * (1.0 - (cpu - 5.0) / 90.0), 0.5, 200.0)
        cache    = _clamp(0.95 - 0.06 * wave, 0.82, 0.999)
        db_conns = max(1, qps // 8)
        db_size  = STORAGE_GB[idx] * 0.4 + month * 3.0 + i * 0.8

        rpm      = int(_clamp(BASE_RPM[idx] * (0.75 + 0.5 * ((cpu - 5.0) / 90.0)), 10, 8000))
        err_pct  = _clamp(0.4 + 2.5 * max(0.0, wave), 0.05, 8.0)
        resp_ms  = _clamp(40.0 + 220.0 * (1.0 - (cpu - 5.0) / 90.0), 15.0, 900.0)

        day_of_month = min(1 + (i * days_in_month // count), days_in_month)
        hour         = i * 23 // count
        minute       = (i * 7) % 60
        event_id     = f"evt-{prefix}-{year}-{month:02d}-{i+1:02d}"

        records.append({
            "eventId":   event_id,
            "timestamp": _ts(year, month, day_of_month, hour, minute),
            "company":   {"companyName": company},
            "agent": {
                "agentId":   AGENT_IDS[idx],
                "hostname":  f"{AGENT_IDS[idx]}.internal",
                "ipAddress": agent_ips[idx],
            },
            "resource": {
                "resourceType":    "EC2",
                "resourceId":      f"res-{event_id}",
                "environment":     "PRODUCTION",
                "region":          "ap-south-1",
                "availabilityZone":"ap-south-1a",
            },
            "compute": {
                "cpuUsagePercent": _r2(cpu),
                "cpuCores":        CPU_CORES[idx],
                "loadAverage1m":   _r2(cpu / 10.0),
                "loadAverage5m":   _r2(cpu / 12.0),
                "loadAverage15m":  _r2(cpu / 15.0),
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
                "diskReadMB":       _r2(disk_r),
                "diskWriteMB":      _r2(disk_w),
                "diskUsagePercent": _r2(disk_pct),
                "storageUsedGB":    _r2(storage),
            },
            "database": {
                "activeConnections":  db_conns,
                "queriesPerSecond":   qps,
                "averageQueryTimeMs": _r2(query_ms),
                "cacheHitRatio":      _r4(cache),
                "databaseSizeGB":     _r2(db_size),
            },
            "application": {
                "requestsPerMinute": rpm,
                "errorRatePercent":  _r3(err_pct),
                "responseTimeMs":    _r2(resp_ms),
            },
        })

    return records


def build_all():
    companies  = [("amazon", "amz", "10.0"), ("vercel", "vcl", "10.1")]
    year_months = [(2026, 4), (2026, 5), (2026, 6)]
    all_records = []
    for company, prefix, ip_net in companies:
        for year, month in year_months:
            all_records.extend(generate_month(company, prefix, ip_net, year, month))
    return all_records


if __name__ == "__main__":
    records = build_all()

    here        = Path(__file__).resolve().parent          # audit-ml-service/
    project_root = here.parent                              # CloudCostOps/
    java_res    = project_root / "OrchestrationEngine" / "src" / "main" / "resources"

    # Write to project root (read by Python)
    root_json = project_root / "seed_records.json"
    root_json.write_text(json.dumps(records, indent=2), encoding="utf-8")
    print(f"Written {len(records)} records → {root_json}")

    # Copy to Java classpath resources (read by DataSeeder)
    java_json = java_res / "seed_records.json"
    shutil.copy(root_json, java_json)
    print(f"Copied  {len(records)} records → {java_json}")
