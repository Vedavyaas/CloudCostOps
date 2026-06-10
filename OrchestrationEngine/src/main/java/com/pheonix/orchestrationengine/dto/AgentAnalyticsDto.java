package com.pheonix.orchestrationengine.dto;

public class AgentAnalyticsDto {

    private String agentId;
    private String hostName;
    private String ipAddress;
    private int    sampleCount;

    // Audit-sourced averages
    private double avgTotalCost;
    private double totalCostSum;
    private double avgDailyCost;
    private double avgMonthlyCost;

    private double avgCpuUsage;
    private double peakCpuUsage;
    private double avgMemoryUsage;
    private double peakMemoryUsage;
    private double avgDiskUsage;
    private double avgNetworkUsage;

    private double avgResponseTime;
    private double avgErrorTrend;
    private double avgAvailabilityScore;
    private double avgApplicationHealthScore;

    private double avgQPS;
    private double peakQPS;
    private double avgCacheEfficiency;
    private double avgQueryEfficiency;

    private double avgResourceUtilizationScore;
    private double avgOverallAnomalyScore;

    public AgentAnalyticsDto() {}

    // ── Getters / Setters ──────────────────────────────────────────────────

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public int getSampleCount() { return sampleCount; }
    public void setSampleCount(int sampleCount) { this.sampleCount = sampleCount; }

    public double getAvgTotalCost() { return avgTotalCost; }
    public void setAvgTotalCost(double avgTotalCost) { this.avgTotalCost = avgTotalCost; }

    public double getTotalCostSum() { return totalCostSum; }
    public void setTotalCostSum(double totalCostSum) { this.totalCostSum = totalCostSum; }

    public double getAvgDailyCost() { return avgDailyCost; }
    public void setAvgDailyCost(double avgDailyCost) { this.avgDailyCost = avgDailyCost; }

    public double getAvgMonthlyCost() { return avgMonthlyCost; }
    public void setAvgMonthlyCost(double avgMonthlyCost) { this.avgMonthlyCost = avgMonthlyCost; }

    public double getAvgCpuUsage() { return avgCpuUsage; }
    public void setAvgCpuUsage(double avgCpuUsage) { this.avgCpuUsage = avgCpuUsage; }

    public double getPeakCpuUsage() { return peakCpuUsage; }
    public void setPeakCpuUsage(double peakCpuUsage) { this.peakCpuUsage = peakCpuUsage; }

    public double getAvgMemoryUsage() { return avgMemoryUsage; }
    public void setAvgMemoryUsage(double avgMemoryUsage) { this.avgMemoryUsage = avgMemoryUsage; }

    public double getPeakMemoryUsage() { return peakMemoryUsage; }
    public void setPeakMemoryUsage(double peakMemoryUsage) { this.peakMemoryUsage = peakMemoryUsage; }

    public double getAvgDiskUsage() { return avgDiskUsage; }
    public void setAvgDiskUsage(double avgDiskUsage) { this.avgDiskUsage = avgDiskUsage; }

    public double getAvgNetworkUsage() { return avgNetworkUsage; }
    public void setAvgNetworkUsage(double avgNetworkUsage) { this.avgNetworkUsage = avgNetworkUsage; }

    public double getAvgResponseTime() { return avgResponseTime; }
    public void setAvgResponseTime(double avgResponseTime) { this.avgResponseTime = avgResponseTime; }

    public double getAvgErrorTrend() { return avgErrorTrend; }
    public void setAvgErrorTrend(double avgErrorTrend) { this.avgErrorTrend = avgErrorTrend; }

    public double getAvgAvailabilityScore() { return avgAvailabilityScore; }
    public void setAvgAvailabilityScore(double avgAvailabilityScore) { this.avgAvailabilityScore = avgAvailabilityScore; }

    public double getAvgApplicationHealthScore() { return avgApplicationHealthScore; }
    public void setAvgApplicationHealthScore(double avgApplicationHealthScore) { this.avgApplicationHealthScore = avgApplicationHealthScore; }

    public double getAvgQPS() { return avgQPS; }
    public void setAvgQPS(double avgQPS) { this.avgQPS = avgQPS; }

    public double getPeakQPS() { return peakQPS; }
    public void setPeakQPS(double peakQPS) { this.peakQPS = peakQPS; }

    public double getAvgCacheEfficiency() { return avgCacheEfficiency; }
    public void setAvgCacheEfficiency(double avgCacheEfficiency) { this.avgCacheEfficiency = avgCacheEfficiency; }

    public double getAvgQueryEfficiency() { return avgQueryEfficiency; }
    public void setAvgQueryEfficiency(double avgQueryEfficiency) { this.avgQueryEfficiency = avgQueryEfficiency; }

    public double getAvgResourceUtilizationScore() { return avgResourceUtilizationScore; }
    public void setAvgResourceUtilizationScore(double avgResourceUtilizationScore) { this.avgResourceUtilizationScore = avgResourceUtilizationScore; }

    public double getAvgOverallAnomalyScore() { return avgOverallAnomalyScore; }
    public void setAvgOverallAnomalyScore(double avgOverallAnomalyScore) { this.avgOverallAnomalyScore = avgOverallAnomalyScore; }
}
