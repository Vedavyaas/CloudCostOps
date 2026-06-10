package com.pheonix.orchestrationengine.input_metrics;

public record CloudAuditMetricEvent(
        String eventId,
        String auditTimestamp,

        // --- Cost Analytics ---
        double totalEstimatedCost,
        double computeCostPercentage,
        double memoryCostPercentage,
        double networkCostPercentage,
        double storageCostPercentage,
        double costPerRequest,
        double costPerQuery,
        double dailyCost,
        double weeklyCost,
        double monthlyCost,
        double costGrowthRate,

        // --- Utilization Analytics ---
        double resourceUtilizationScore,
        double averageCpuUsage,
        double peakCpuUsage,
        double averageMemoryUsage,
        double peakMemoryUsage,
        double averageDiskUsage,
        double averageNetworkUsage,

        // --- Database Analytics ---
        double connectionUtilization,
        double queryEfficiencyScore,
        double averageQPS,
        double peakQPS,
        double databaseGrowthRate,
        double cacheEfficiencyScore,

        // --- Application Analytics ---
        double availabilityScore,
        double errorTrend,
        double requestGrowthRate,
        double averageResponseTime,
        double peakResponseTime,
        double applicationHealthScore,

        // --- Capacity Planning Metrics ---
        int daysUntilStorageFull,
        double projectedMonthlyTraffic,
        double projectedMonthlyCost,
        double cpuGrowthRate,
        double memoryGrowthRate,

        // --- Anomaly Detection Inputs ---
        double cpuAnomalyScore,
        double memoryAnomalyScore,
        double networkAnomalyScore,
        double databaseAnomalyScore,
        double responseTimeAnomalyScore,
        double overallAnomalyScore
) {
}
