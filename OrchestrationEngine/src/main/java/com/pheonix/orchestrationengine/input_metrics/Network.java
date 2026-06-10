package com.pheonix.orchestrationengine.input_metrics;

public record Network (
        double networkInMB,
        double networkOutMB,
        int activeConnections
) {
}
