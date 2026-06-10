package com.pheonix.orchestrationengine.input_metrics;

public record Application(
        int requestsPerMinute,
        double errorRatePercent,
        double responseTimeMs
) {
}
