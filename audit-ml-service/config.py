import os
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent
MODEL_DIR = BASE_DIR / "models"
MODEL_PATH = MODEL_DIR / "audit_predictor.joblib"
METADATA_PATH = MODEL_DIR / "model_metadata.json"

KAFKA_BOOTSTRAP = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
INPUT_TOPIC = os.getenv("KAFKA_INPUT_TOPIC", "cloud_metrics")
OUTPUT_TOPIC = os.getenv("KAFKA_OUTPUT_TOPIC", "cloud_audit_metric")
CONSUMER_GROUP = os.getenv("KAFKA_CONSUMER_GROUP", "audit_ml_group_2")

# Brief delay so OrchestrationEngine can persist cloud_metrics before audit arrives.
PUBLISH_DELAY_SECONDS = float(os.getenv("PUBLISH_DELAY_SECONDS", "1.5"))

TRAINING_SAMPLES = int(os.getenv("TRAINING_SAMPLES", "30000"))
RANDOM_SEED = 42
