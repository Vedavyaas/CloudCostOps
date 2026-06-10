package com.pheonix.orchestrationengine.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
public class CloudAuditMetricEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String eventId;

    private Instant auditTimestamp;

    // --- Cost Analytics (pre-calculated by the audit producer/agent, not computed here) ---
    private double totalEstimatedCost;
    private double computeCostPercentage;
    private double memoryCostPercentage;
    private double networkCostPercentage;
    private double storageCostPercentage;
    private double costPerRequest;
    private double costPerQuery;
    private double dailyCost;
    private double weeklyCost;
    private double monthlyCost;
    private double costGrowthRate;

    // --- Utilization Analytics (pre-calculated by the audit producer/agent, not computed here) ---
    private double resourceUtilizationScore; // (CPU + Memory + Disk) / 3 — computed by producer
    private double averageCpuUsage;
    private double peakCpuUsage;
    private double averageMemoryUsage;
    private double peakMemoryUsage;
    private double averageDiskUsage;
    private double averageNetworkUsage;

    // --- Database Analytics (pre-calculated by the audit producer/agent, not computed here) ---
    private double connectionUtilization;
    private double queryEfficiencyScore; // queriesPerSecond / averageQueryTimeMs — computed by producer
    private double averageQPS;
    private double peakQPS;
    private double databaseGrowthRate;
    private double cacheEfficiencyScore;

    // --- Application Analytics (pre-calculated by the audit producer/agent, not computed here) ---
    private double availabilityScore;
    private double errorTrend;
    private double requestGrowthRate;
    private double averageResponseTime;
    private double peakResponseTime;
    private double applicationHealthScore; // 100 - errorRatePenalty - responseTimePenalty — computed by producer

    // --- Capacity Planning Metrics (pre-calculated by the audit producer/agent, not computed here) ---
    private int daysUntilStorageFull;
    private double projectedMonthlyTraffic;
    private double projectedMonthlyCost;
    private double cpuGrowthRate;
    private double memoryGrowthRate;

    // --- Anomaly Detection Inputs (pre-calculated by the audit producer/agent, not computed here) ---
    private double cpuAnomalyScore;
    private double memoryAnomalyScore;
    private double networkAnomalyScore;
    private double databaseAnomalyScore;
    private double responseTimeAnomalyScore;
    private double overallAnomalyScore;

    public CloudAuditMetricEntity() {
    }

    public CloudAuditMetricEntity(
            String eventId,
            Instant auditTimestamp,
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
            double resourceUtilizationScore,
            double averageCpuUsage,
            double peakCpuUsage,
            double averageMemoryUsage,
            double peakMemoryUsage,
            double averageDiskUsage,
            double averageNetworkUsage,
            double connectionUtilization,
            double queryEfficiencyScore,
            double averageQPS,
            double peakQPS,
            double databaseGrowthRate,
            double cacheEfficiencyScore,
            double availabilityScore,
            double errorTrend,
            double requestGrowthRate,
            double averageResponseTime,
            double peakResponseTime,
            double applicationHealthScore,
            int daysUntilStorageFull,
            double projectedMonthlyTraffic,
            double projectedMonthlyCost,
            double cpuGrowthRate,
            double memoryGrowthRate,
            double cpuAnomalyScore,
            double memoryAnomalyScore,
            double networkAnomalyScore,
            double databaseAnomalyScore,
            double responseTimeAnomalyScore,
            double overallAnomalyScore
    ) {
        this.eventId = eventId;
        this.auditTimestamp = auditTimestamp;
        this.totalEstimatedCost = totalEstimatedCost;
        this.computeCostPercentage = computeCostPercentage;
        this.memoryCostPercentage = memoryCostPercentage;
        this.networkCostPercentage = networkCostPercentage;
        this.storageCostPercentage = storageCostPercentage;
        this.costPerRequest = costPerRequest;
        this.costPerQuery = costPerQuery;
        this.dailyCost = dailyCost;
        this.weeklyCost = weeklyCost;
        this.monthlyCost = monthlyCost;
        this.costGrowthRate = costGrowthRate;
        this.resourceUtilizationScore = resourceUtilizationScore;
        this.averageCpuUsage = averageCpuUsage;
        this.peakCpuUsage = peakCpuUsage;
        this.averageMemoryUsage = averageMemoryUsage;
        this.peakMemoryUsage = peakMemoryUsage;
        this.averageDiskUsage = averageDiskUsage;
        this.averageNetworkUsage = averageNetworkUsage;
        this.connectionUtilization = connectionUtilization;
        this.queryEfficiencyScore = queryEfficiencyScore;
        this.averageQPS = averageQPS;
        this.peakQPS = peakQPS;
        this.databaseGrowthRate = databaseGrowthRate;
        this.cacheEfficiencyScore = cacheEfficiencyScore;
        this.availabilityScore = availabilityScore;
        this.errorTrend = errorTrend;
        this.requestGrowthRate = requestGrowthRate;
        this.averageResponseTime = averageResponseTime;
        this.peakResponseTime = peakResponseTime;
        this.applicationHealthScore = applicationHealthScore;
        this.daysUntilStorageFull = daysUntilStorageFull;
        this.projectedMonthlyTraffic = projectedMonthlyTraffic;
        this.projectedMonthlyCost = projectedMonthlyCost;
        this.cpuGrowthRate = cpuGrowthRate;
        this.memoryGrowthRate = memoryGrowthRate;
        this.cpuAnomalyScore = cpuAnomalyScore;
        this.memoryAnomalyScore = memoryAnomalyScore;
        this.networkAnomalyScore = networkAnomalyScore;
        this.databaseAnomalyScore = databaseAnomalyScore;
        this.responseTimeAnomalyScore = responseTimeAnomalyScore;
        this.overallAnomalyScore = overallAnomalyScore;
    }

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public Instant getAuditTimestamp() { return auditTimestamp; }
    public void setAuditTimestamp(Instant auditTimestamp) { this.auditTimestamp = auditTimestamp; }

    public double getTotalEstimatedCost() { return totalEstimatedCost; }
    public void setTotalEstimatedCost(double totalEstimatedCost) { this.totalEstimatedCost = totalEstimatedCost; }

    public double getComputeCostPercentage() { return computeCostPercentage; }
    public void setComputeCostPercentage(double computeCostPercentage) { this.computeCostPercentage = computeCostPercentage; }

    public double getMemoryCostPercentage() { return memoryCostPercentage; }
    public void setMemoryCostPercentage(double memoryCostPercentage) { this.memoryCostPercentage = memoryCostPercentage; }

    public double getNetworkCostPercentage() { return networkCostPercentage; }
    public void setNetworkCostPercentage(double networkCostPercentage) { this.networkCostPercentage = networkCostPercentage; }

    public double getStorageCostPercentage() { return storageCostPercentage; }
    public void setStorageCostPercentage(double storageCostPercentage) { this.storageCostPercentage = storageCostPercentage; }

    public double getCostPerRequest() { return costPerRequest; }
    public void setCostPerRequest(double costPerRequest) { this.costPerRequest = costPerRequest; }

    public double getCostPerQuery() { return costPerQuery; }
    public void setCostPerQuery(double costPerQuery) { this.costPerQuery = costPerQuery; }

    public double getDailyCost() { return dailyCost; }
    public void setDailyCost(double dailyCost) { this.dailyCost = dailyCost; }

    public double getWeeklyCost() { return weeklyCost; }
    public void setWeeklyCost(double weeklyCost) { this.weeklyCost = weeklyCost; }

    public double getMonthlyCost() { return monthlyCost; }
    public void setMonthlyCost(double monthlyCost) { this.monthlyCost = monthlyCost; }

    public double getCostGrowthRate() { return costGrowthRate; }
    public void setCostGrowthRate(double costGrowthRate) { this.costGrowthRate = costGrowthRate; }

    public double getResourceUtilizationScore() { return resourceUtilizationScore; }
    public void setResourceUtilizationScore(double resourceUtilizationScore) { this.resourceUtilizationScore = resourceUtilizationScore; }

    public double getAverageCpuUsage() { return averageCpuUsage; }
    public void setAverageCpuUsage(double averageCpuUsage) { this.averageCpuUsage = averageCpuUsage; }

    public double getPeakCpuUsage() { return peakCpuUsage; }
    public void setPeakCpuUsage(double peakCpuUsage) { this.peakCpuUsage = peakCpuUsage; }

    public double getAverageMemoryUsage() { return averageMemoryUsage; }
    public void setAverageMemoryUsage(double averageMemoryUsage) { this.averageMemoryUsage = averageMemoryUsage; }

    public double getPeakMemoryUsage() { return peakMemoryUsage; }
    public void setPeakMemoryUsage(double peakMemoryUsage) { this.peakMemoryUsage = peakMemoryUsage; }

    public double getAverageDiskUsage() { return averageDiskUsage; }
    public void setAverageDiskUsage(double averageDiskUsage) { this.averageDiskUsage = averageDiskUsage; }

    public double getAverageNetworkUsage() { return averageNetworkUsage; }
    public void setAverageNetworkUsage(double averageNetworkUsage) { this.averageNetworkUsage = averageNetworkUsage; }

    public double getConnectionUtilization() { return connectionUtilization; }
    public void setConnectionUtilization(double connectionUtilization) { this.connectionUtilization = connectionUtilization; }

    public double getQueryEfficiencyScore() { return queryEfficiencyScore; }
    public void setQueryEfficiencyScore(double queryEfficiencyScore) { this.queryEfficiencyScore = queryEfficiencyScore; }

    public double getAverageQPS() { return averageQPS; }
    public void setAverageQPS(double averageQPS) { this.averageQPS = averageQPS; }

    public double getPeakQPS() { return peakQPS; }
    public void setPeakQPS(double peakQPS) { this.peakQPS = peakQPS; }

    public double getDatabaseGrowthRate() { return databaseGrowthRate; }
    public void setDatabaseGrowthRate(double databaseGrowthRate) { this.databaseGrowthRate = databaseGrowthRate; }

    public double getCacheEfficiencyScore() { return cacheEfficiencyScore; }
    public void setCacheEfficiencyScore(double cacheEfficiencyScore) { this.cacheEfficiencyScore = cacheEfficiencyScore; }

    public double getAvailabilityScore() { return availabilityScore; }
    public void setAvailabilityScore(double availabilityScore) { this.availabilityScore = availabilityScore; }

    public double getErrorTrend() { return errorTrend; }
    public void setErrorTrend(double errorTrend) { this.errorTrend = errorTrend; }

    public double getRequestGrowthRate() { return requestGrowthRate; }
    public void setRequestGrowthRate(double requestGrowthRate) { this.requestGrowthRate = requestGrowthRate; }

    public double getAverageResponseTime() { return averageResponseTime; }
    public void setAverageResponseTime(double averageResponseTime) { this.averageResponseTime = averageResponseTime; }

    public double getPeakResponseTime() { return peakResponseTime; }
    public void setPeakResponseTime(double peakResponseTime) { this.peakResponseTime = peakResponseTime; }

    public double getApplicationHealthScore() { return applicationHealthScore; }
    public void setApplicationHealthScore(double applicationHealthScore) { this.applicationHealthScore = applicationHealthScore; }

    public int getDaysUntilStorageFull() { return daysUntilStorageFull; }
    public void setDaysUntilStorageFull(int daysUntilStorageFull) { this.daysUntilStorageFull = daysUntilStorageFull; }

    public double getProjectedMonthlyTraffic() { return projectedMonthlyTraffic; }
    public void setProjectedMonthlyTraffic(double projectedMonthlyTraffic) { this.projectedMonthlyTraffic = projectedMonthlyTraffic; }

    public double getProjectedMonthlyCost() { return projectedMonthlyCost; }
    public void setProjectedMonthlyCost(double projectedMonthlyCost) { this.projectedMonthlyCost = projectedMonthlyCost; }

    public double getCpuGrowthRate() { return cpuGrowthRate; }
    public void setCpuGrowthRate(double cpuGrowthRate) { this.cpuGrowthRate = cpuGrowthRate; }

    public double getMemoryGrowthRate() { return memoryGrowthRate; }
    public void setMemoryGrowthRate(double memoryGrowthRate) { this.memoryGrowthRate = memoryGrowthRate; }

    public double getCpuAnomalyScore() { return cpuAnomalyScore; }
    public void setCpuAnomalyScore(double cpuAnomalyScore) { this.cpuAnomalyScore = cpuAnomalyScore; }

    public double getMemoryAnomalyScore() { return memoryAnomalyScore; }
    public void setMemoryAnomalyScore(double memoryAnomalyScore) { this.memoryAnomalyScore = memoryAnomalyScore; }

    public double getNetworkAnomalyScore() { return networkAnomalyScore; }
    public void setNetworkAnomalyScore(double networkAnomalyScore) { this.networkAnomalyScore = networkAnomalyScore; }

    public double getDatabaseAnomalyScore() { return databaseAnomalyScore; }
    public void setDatabaseAnomalyScore(double databaseAnomalyScore) { this.databaseAnomalyScore = databaseAnomalyScore; }

    public double getResponseTimeAnomalyScore() { return responseTimeAnomalyScore; }
    public void setResponseTimeAnomalyScore(double responseTimeAnomalyScore) { this.responseTimeAnomalyScore = responseTimeAnomalyScore; }

    public double getOverallAnomalyScore() { return overallAnomalyScore; }
    public void setOverallAnomalyScore(double overallAnomalyScore) { this.overallAnomalyScore = overallAnomalyScore; }
}
