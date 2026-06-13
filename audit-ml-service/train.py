"""Train the multi-output audit metric regression model."""

from __future__ import annotations

import json
from pathlib import Path

import joblib
import numpy as np
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_absolute_error, r2_score
from sklearn.model_selection import train_test_split
from sklearn.multioutput import MultiOutputRegressor
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler

from config import METADATA_PATH, MODEL_DIR, MODEL_PATH, RANDOM_SEED, TRAINING_SAMPLES
from features import FEATURE_NAMES
from label_generator import OUTPUT_FIELDS, build_training_dataset


def train_model(sample_count: int = TRAINING_SAMPLES) -> Pipeline:
    print(f"[train] Generating {sample_count} synthetic training samples...")
    x_rows, y_rows = build_training_dataset(sample_count, seed=RANDOM_SEED)
    x = np.array(x_rows, dtype=np.float64)
    y = np.array(y_rows, dtype=np.float64)

    x_train, x_test, y_train, y_test = train_test_split(
        x, y, test_size=0.15, random_state=RANDOM_SEED
    )

    model = Pipeline(
        steps=[
            ("scaler", StandardScaler()),
            (
                "regressor",
                MultiOutputRegressor(
                    RandomForestRegressor(
                        n_estimators=120,
                        max_depth=18,
                        min_samples_leaf=2,
                        random_state=RANDOM_SEED,
                        n_jobs=-1,
                    )
                ),
            ),
        ]
    )

    print("[train] Fitting RandomForest multi-output regressor...")
    model.fit(x_train, y_train)

    predictions = model.predict(x_test)
    mae = mean_absolute_error(y_test, predictions)
    r2 = r2_score(y_test, predictions)
    print(f"[train] Test MAE: {mae:.4f}  |  Test R²: {r2:.4f}")

    MODEL_DIR.mkdir(parents=True, exist_ok=True)
    joblib.dump(model, MODEL_PATH)

    metadata = {
        "feature_names": FEATURE_NAMES,
        "output_fields": OUTPUT_FIELDS,
        "training_samples": sample_count,
        "test_mae": mae,
        "test_r2": r2,
        "model_type": "RandomForestRegressor (MultiOutput)",
    }
    METADATA_PATH.write_text(json.dumps(metadata, indent=2))
    print(f"[train] Saved model → {MODEL_PATH}")
    return model


if __name__ == "__main__":
    train_model()
