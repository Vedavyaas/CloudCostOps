package com.pheonix.orchestrationengine.input_metrics;

public record Agent (
        String agentId,
        String hostname,
        String ipAddress
){
}

