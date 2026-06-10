package com.pheonix.orchestrationengine.dto;

public class AnalyticsSummaryDto {
    private String companyName;
    private double totalCost;
    private double currentMonthCost;
    private double cpuAvg;
    private double memAvg;
    private int totalSamples;

    public AnalyticsSummaryDto() {}

    public AnalyticsSummaryDto(String companyName, double totalCost, double currentMonthCost, double cpuAvg, double memAvg, int totalSamples) {
        this.companyName = companyName;
        this.totalCost = totalCost;
        this.currentMonthCost = currentMonthCost;
        this.cpuAvg = cpuAvg;
        this.memAvg = memAvg;
        this.totalSamples = totalSamples;
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public double getCurrentMonthCost() { return currentMonthCost; }
    public void setCurrentMonthCost(double currentMonthCost) { this.currentMonthCost = currentMonthCost; }

    public double getCpuAvg() { return cpuAvg; }
    public void setCpuAvg(double cpuAvg) { this.cpuAvg = cpuAvg; }

    public double getMemAvg() { return memAvg; }
    public void setMemAvg(double memAvg) { this.memAvg = memAvg; }

    public int getTotalSamples() { return totalSamples; }
    public void setTotalSamples(int totalSamples) { this.totalSamples = totalSamples; }
}
