package com.pheonix.orchestrationengine.input_metrics;

public record Disk(
        double diskReadMB,
        double diskWriteMB,
        double diskUsagePercent,
        double storageUsedGB
) {
}
