#!/usr/bin/env python3
"""Entry point for the CloudCostOps audit ML Kafka service."""

import argparse
import json
import sys

from label_generator import generate_cloud_metric_sample, derive_audit_labels
from predictor import build_audit_event, predict_audit_metrics


def cmd_train(_args):
    from train import train_model

    train_model()
    return 0


def cmd_predict(args):
    with open(args.file, encoding="utf-8") as handle:
        payload = json.load(handle)
    audit = build_audit_event(payload)
    print(json.dumps(audit, indent=2))
    return 0


def cmd_demo(_args):
    import random

    sample = generate_cloud_metric_sample(random.Random(7))
    predicted = predict_audit_metrics(sample)
    expected = derive_audit_labels(sample, random.Random(7))

    print("=== Input (cloud_metrics) ===")
    print(json.dumps(sample, indent=2))
    print("\n=== ML Prediction (cloud_audit_metric) ===")
    print(json.dumps(predicted, indent=2))
    print("\n=== Formula baseline (training labels) ===")
    print(json.dumps(expected, indent=2))
    return 0


def cmd_serve(_args):
    from kafka_bridge import run_bridge

    run_bridge()
    return 0


def main():
    parser = argparse.ArgumentParser(description="CloudCostOps Audit ML Service")
    sub = parser.add_subparsers(dest="command", required=True)

    sub.add_parser("train", help="Train the audit metric model")
    sub.add_parser("serve", help="Run Kafka consumer/producer bridge")
    sub.add_parser("demo", help="Run a local prediction demo")

    predict_parser = sub.add_parser("predict", help="Predict audit metrics from a JSON file")
    predict_parser.add_argument("file", help="Path to cloud_metrics JSON file")

    args = parser.parse_args()
    handlers = {
        "train": cmd_train,
        "serve": cmd_serve,
        "demo": cmd_demo,
        "predict": cmd_predict,
    }
    return handlers[args.command](args)


if __name__ == "__main__":
    sys.exit(main())
