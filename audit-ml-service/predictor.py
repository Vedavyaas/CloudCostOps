"""Load the trained model and produce cloud_audit_metric predictions."""

from __future__ import annotations

from typing import Any

import joblib
import numpy as np

from config import MODEL_PATH
from features import cloud_metric_to_features
from label_generator import OUTPUT_FIELDS

_model = None


def _load_model():
    global _model
    if _model is None:
        if not MODEL_PATH.exists():
            raise FileNotFoundError(
                f"Model not found at {MODEL_PATH}. Run: python train.py"
            )
        _model = joblib.load(MODEL_PATH)
    return _model


def _clamp(value: float, low: float, high: float) -> float:
    return max(low, min(high, value))


def _post_process(raw: dict[str, float]) -> dict[str, float]:
    """Enforce domain constraints on model outputs."""
    out = dict(raw)

    # Cost breakdown percentages should sum to ~100
    pct_keys = [
        "computeCostPercentage",
        "memoryCostPercentage",
        "networkCostPercentage",
        "storageCostPercentage",
    ]
    pct_sum = sum(out[k] for k in pct_keys)
    if pct_sum > 0:
        for k in pct_keys:
            out[k] = out[k] / pct_sum * 100.0

    out["computeCostPercentage"] = _clamp(out["computeCostPercentage"], 0.0, 100.0)
    out["memoryCostPercentage"] = _clamp(out["memoryCostPercentage"], 0.0, 100.0)
    out["networkCostPercentage"] = _clamp(out["networkCostPercentage"], 0.0, 100.0)
    out["storageCostPercentage"] = _clamp(out["storageCostPercentage"], 0.0, 100.0)

    for key in (
        "averageCpuUsage",
        "peakCpuUsage",
        "averageMemoryUsage",
        "peakMemoryUsage",
        "averageDiskUsage",
        "resourceUtilizationScore",
        "cacheEfficiencyScore",
        "availabilityScore",
        "applicationHealthScore",
    ):
        out[key] = _clamp(out[key], 0.0, 100.0)

    out["connectionUtilization"] = _clamp(out["connectionUtilization"], 0.0, 1.0)

    for key in (
        "cpuAnomalyScore",
        "memoryAnomalyScore",
        "networkAnomalyScore",
        "databaseAnomalyScore",
        "responseTimeAnomalyScore",
        "overallAnomalyScore",
    ):
        out[key] = _clamp(out[key], 0.0, 1.0)

    out["daysUntilStorageFull"] = int(_clamp(out["daysUntilStorageFull"], 1.0, 365.0))

    for key in (
        "totalEstimatedCost",
        "dailyCost",
        "weeklyCost",
        "monthlyCost",
        "projectedMonthlyCost",
        "projectedMonthlyTraffic",
        "averageQPS",
        "peakQPS",
        "averageResponseTime",
        "peakResponseTime",
    ):
        out[key] = max(0.0, out[key])

    # Round for stable JSON output
    for key, value in out.items():
        if key == "daysUntilStorageFull":
            out[key] = int(value)
        elif key in ("costPerRequest", "costPerQuery"):
            out[key] = round(value, 6)
        elif key.endswith("Score") or "Percentage" in key or key.startswith("average") or key.startswith("peak"):
            out[key] = round(value, 2)
        else:
            out[key] = round(value, 3)

    return out


def predict_audit_metrics(cloud_metric: dict[str, Any]) -> dict[str, Any]:
    model = _load_model()
    features = np.array([cloud_metric_to_features(cloud_metric)], dtype=np.float64)
    prediction = model.predict(features)[0]

    raw = {field: float(prediction[i]) for i, field in enumerate(OUTPUT_FIELDS)}
    return _post_process(raw)


def build_audit_event(cloud_metric: dict[str, Any]) -> dict[str, Any]:
    from datetime import datetime, timezone

    event_id = cloud_metric.get("eventId")
    if not event_id:
        raise ValueError("cloud_metrics payload must include eventId")

    audit = predict_audit_metrics(cloud_metric)
    return {
        "eventId": event_id,
        "auditTimestamp": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
        **audit,
    }
