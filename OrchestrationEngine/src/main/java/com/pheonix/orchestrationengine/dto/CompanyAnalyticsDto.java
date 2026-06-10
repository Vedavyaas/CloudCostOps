package com.pheonix.orchestrationengine.dto;

import java.util.List;
import java.util.Map;

public class CompanyAnalyticsDto {

    // ── Headline KPIs ─────────────────────────────────────────────────────────
    private String companyName;
    private int    totalSamples;

    // Costs
    private double totalAllTimeCost;
    private double currentMonthCost;
    private double previousMonthCost;
    private double todayCost;
    private double monthOverMonthChangePercent;   // + means increase

    // Compute
    private double avgCpuPercent;
    private double peakCpuPercent;
    private double avgLoadAvg1m;

    // Memory
    private double avgMemPercent;
    private double avgMemUsedGB;
    private double peakMemPercent;

    // Disk
    private double avgDiskReadMBps;
    private double avgDiskWriteMBps;
    private double avgDiskUsagePercent;
    private double avgStorageUsedGB;

    // Network
    private double avgNetworkInMBps;
    private double avgNetworkOutMBps;
    private double avgActiveConnections;

    // Application
    private double avgRequestsPerMin;
    private double avgErrorRatePercent;
    private double avgResponseTimeMs;

    // Database
    private double avgDbQueryTimeMs;
    private double avgCacheHitRatio;
    private double avgDbSizeGB;
    private double avgDbQueriesPerSec;

    // ── Time-series data (for sparklines / bar charts) ─────────────────────
    // Each entry: { label:"Jun 10", cost:42.3, cpu:67.2, mem:55.1, ... }
    private List<DailySnapshot> dailySnapshots;   // last 30 days
    private List<MonthlySnapshot> monthlySnapshots; // last 12 months

    // ── Resource breakdown ─────────────────────────────────────────────────
    // e.g. { "Compute":45.2, "Memory":20.0, "Storage":12.3, "Network":22.5 }
    private Map<String, Double> costBreakdownPercent;

    // ── Echoed back so the UI can pre-populate the weight inputs ───────────
    private CostWeightsDto appliedWeights;

    // ─────── Inner snapshot types ──────────────────────────────────────────

    public static class DailySnapshot {
        private String date;       // "2025-06-10"
        private double cost;
        private double cpuAvg;
        private double memAvg;
        private double diskUsageAvg;
        private double networkIn;
        private double networkOut;
        private double errorRate;
        private int    samples;

        public DailySnapshot() {}
        public DailySnapshot(String date, double cost, double cpuAvg, double memAvg,
                             double diskUsageAvg, double networkIn, double networkOut,
                             double errorRate, int samples) {
            this.date = date; this.cost = cost; this.cpuAvg = cpuAvg;
            this.memAvg = memAvg; this.diskUsageAvg = diskUsageAvg;
            this.networkIn = networkIn; this.networkOut = networkOut;
            this.errorRate = errorRate; this.samples = samples;
        }

        public String getDate() { return date; }
        public double getCost() { return cost; }
        public double getCpuAvg() { return cpuAvg; }
        public double getMemAvg() { return memAvg; }
        public double getDiskUsageAvg() { return diskUsageAvg; }
        public double getNetworkIn() { return networkIn; }
        public double getNetworkOut() { return networkOut; }
        public double getErrorRate() { return errorRate; }
        public int getSamples() { return samples; }
    }

    public static class MonthlySnapshot {
        private String month;      // "2025-06"
        private double cost;
        private double cpuAvg;
        private double memAvg;
        private int    samples;

        public MonthlySnapshot() {}
        public MonthlySnapshot(String month, double cost, double cpuAvg, double memAvg, int samples) {
            this.month = month; this.cost = cost; this.cpuAvg = cpuAvg;
            this.memAvg = memAvg; this.samples = samples;
        }

        public String getMonth() { return month; }
        public double getCost() { return cost; }
        public double getCpuAvg() { return cpuAvg; }
        public double getMemAvg() { return memAvg; }
        public int getSamples() { return samples; }
    }

    // ── Constructor / Getters / Setters ────────────────────────────────────
    public CompanyAnalyticsDto() {}

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String v) { this.companyName = v; }

    public int getTotalSamples() { return totalSamples; }
    public void setTotalSamples(int v) { this.totalSamples = v; }

    public double getTotalAllTimeCost() { return totalAllTimeCost; }
    public void setTotalAllTimeCost(double v) { this.totalAllTimeCost = v; }

    public double getCurrentMonthCost() { return currentMonthCost; }
    public void setCurrentMonthCost(double v) { this.currentMonthCost = v; }

    public double getPreviousMonthCost() { return previousMonthCost; }
    public void setPreviousMonthCost(double v) { this.previousMonthCost = v; }

    public double getTodayCost() { return todayCost; }
    public void setTodayCost(double v) { this.todayCost = v; }

    public double getMonthOverMonthChangePercent() { return monthOverMonthChangePercent; }
    public void setMonthOverMonthChangePercent(double v) { this.monthOverMonthChangePercent = v; }

    public double getAvgCpuPercent() { return avgCpuPercent; }
    public void setAvgCpuPercent(double v) { this.avgCpuPercent = v; }

    public double getPeakCpuPercent() { return peakCpuPercent; }
    public void setPeakCpuPercent(double v) { this.peakCpuPercent = v; }

    public double getAvgLoadAvg1m() { return avgLoadAvg1m; }
    public void setAvgLoadAvg1m(double v) { this.avgLoadAvg1m = v; }

    public double getAvgMemPercent() { return avgMemPercent; }
    public void setAvgMemPercent(double v) { this.avgMemPercent = v; }

    public double getAvgMemUsedGB() { return avgMemUsedGB; }
    public void setAvgMemUsedGB(double v) { this.avgMemUsedGB = v; }

    public double getPeakMemPercent() { return peakMemPercent; }
    public void setPeakMemPercent(double v) { this.peakMemPercent = v; }

    public double getAvgDiskReadMBps() { return avgDiskReadMBps; }
    public void setAvgDiskReadMBps(double v) { this.avgDiskReadMBps = v; }

    public double getAvgDiskWriteMBps() { return avgDiskWriteMBps; }
    public void setAvgDiskWriteMBps(double v) { this.avgDiskWriteMBps = v; }

    public double getAvgDiskUsagePercent() { return avgDiskUsagePercent; }
    public void setAvgDiskUsagePercent(double v) { this.avgDiskUsagePercent = v; }

    public double getAvgStorageUsedGB() { return avgStorageUsedGB; }
    public void setAvgStorageUsedGB(double v) { this.avgStorageUsedGB = v; }

    public double getAvgNetworkInMBps() { return avgNetworkInMBps; }
    public void setAvgNetworkInMBps(double v) { this.avgNetworkInMBps = v; }

    public double getAvgNetworkOutMBps() { return avgNetworkOutMBps; }
    public void setAvgNetworkOutMBps(double v) { this.avgNetworkOutMBps = v; }

    public double getAvgActiveConnections() { return avgActiveConnections; }
    public void setAvgActiveConnections(double v) { this.avgActiveConnections = v; }

    public double getAvgRequestsPerMin() { return avgRequestsPerMin; }
    public void setAvgRequestsPerMin(double v) { this.avgRequestsPerMin = v; }

    public double getAvgErrorRatePercent() { return avgErrorRatePercent; }
    public void setAvgErrorRatePercent(double v) { this.avgErrorRatePercent = v; }

    public double getAvgResponseTimeMs() { return avgResponseTimeMs; }
    public void setAvgResponseTimeMs(double v) { this.avgResponseTimeMs = v; }

    public double getAvgDbQueryTimeMs() { return avgDbQueryTimeMs; }
    public void setAvgDbQueryTimeMs(double v) { this.avgDbQueryTimeMs = v; }

    public double getAvgCacheHitRatio() { return avgCacheHitRatio; }
    public void setAvgCacheHitRatio(double v) { this.avgCacheHitRatio = v; }

    public double getAvgDbSizeGB() { return avgDbSizeGB; }
    public void setAvgDbSizeGB(double v) { this.avgDbSizeGB = v; }

    public double getAvgDbQueriesPerSec() { return avgDbQueriesPerSec; }
    public void setAvgDbQueriesPerSec(double v) { this.avgDbQueriesPerSec = v; }

    public List<DailySnapshot> getDailySnapshots() { return dailySnapshots; }
    public void setDailySnapshots(List<DailySnapshot> v) { this.dailySnapshots = v; }

    public List<MonthlySnapshot> getMonthlySnapshots() { return monthlySnapshots; }
    public void setMonthlySnapshots(List<MonthlySnapshot> v) { this.monthlySnapshots = v; }

    public Map<String, Double> getCostBreakdownPercent() { return costBreakdownPercent; }
    public void setCostBreakdownPercent(Map<String, Double> v) { this.costBreakdownPercent = v; }

    public CostWeightsDto getAppliedWeights() { return appliedWeights; }
    public void setAppliedWeights(CostWeightsDto v) { this.appliedWeights = v; }
}
