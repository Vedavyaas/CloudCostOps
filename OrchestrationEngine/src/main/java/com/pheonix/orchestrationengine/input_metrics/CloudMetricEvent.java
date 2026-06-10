package com.pheonix.orchestrationengine.input_metrics;

public record CloudMetricEvent(
        String eventId,
        Company company,
        Agent agent,
        Resource resource,

        Compute compute,
        Memory memory,
        Network network,
        Disk disk,

        Database database,
        Application application
) {
}
