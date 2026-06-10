package com.pheonix.orchestrationengine.input_metrics;

public record Resource(
        String resourceType,
        String resourceId,
        String environment,
        String region,
        String availabilityZone
) {
}

