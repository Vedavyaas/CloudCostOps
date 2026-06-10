package com.pheonix.orchestrationengine.input_metrics;

public record Database(
        int activeConnections,
        int queriesPerSecond,
        double averageQueryTimeMs,
        double cacheHitRatio,
        double databaseSizeGB
) {
}
