# Audit ML Service

Python ML pipeline that consumes `cloud_metrics` from Kafka, predicts `cloud_audit_metric` fields, and publishes the result for OrchestrationEngine to persist.

## Pipeline

```
cloud_metrics (Kafka)
        │
        ▼
  audit-ml-service  ──► RandomForest multi-output model
        │
        ▼
cloud_audit_metric (Kafka)
        │
        ▼
OrchestrationEngine (links audit to metric by eventId)
```

## Setup

```bash
cd audit-ml-service
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python main.py train
```

## Run

Requires Kafka on `localhost:9092` (same as OrchestrationEngine).

```bash
# Terminal 1 — ML Kafka bridge
python main.py serve

# Terminal 2 — optional: publish a test metric
python publish_sample_metric.py
```

## Commands

| Command | Description |
|---------|-------------|
| `python main.py train` | Train model from 30k synthetic samples |
| `python main.py serve` | Run Kafka consumer → producer bridge |
| `python main.py demo` | Local prediction demo (no Kafka) |
| `python main.py predict <file.json>` | Predict from a cloud_metrics JSON file |

## Environment variables

| Variable | Default | Description |
|----------|---------|-------------|
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker |
| `KAFKA_INPUT_TOPIC` | `cloud_metrics` | Input topic |
| `KAFKA_OUTPUT_TOPIC` | `cloud_audit_metric` | Output topic |
| `PUBLISH_DELAY_SECONDS` | `1.5` | Delay before publishing audit (avoids race with OrchestrationEngine) |
| `TRAINING_SAMPLES` | `30000` | Synthetic samples for training |

## Model

- **Input:** 26 numeric features extracted from nested `cloud_metrics` JSON (compute, memory, network, disk, database, application, resource).
- **Output:** 41 audit fields matching `CloudAuditMetricEvent` in OrchestrationEngine.
- **Algorithm:** `StandardScaler` + `MultiOutputRegressor(RandomForestRegressor)`.
- **Training labels:** Formula-based synthetic labels mirroring OrchestrationEngine audit semantics (utilization scores, cost breakdown, anomaly scores, capacity projections).

Retrain after changing feature engineering or label formulas:

```bash
python main.py train
```
