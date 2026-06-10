package com.pheonix.orchestrationengine.input_metrics;

public record Memory(
        long totalMB,
        long usedMB,
        long freeMB,
        double usagePercent
) {
}
