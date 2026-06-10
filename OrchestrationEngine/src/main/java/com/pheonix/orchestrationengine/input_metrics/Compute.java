package com.pheonix.orchestrationengine.input_metrics;

public record Compute(
        double cpuUsagePercent,
        int cpuCores,
        double loadAverage1m,
        double loadAverage5m,
        double loadAverage15m
) {
}
