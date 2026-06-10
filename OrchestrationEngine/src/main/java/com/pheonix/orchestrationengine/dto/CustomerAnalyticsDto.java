package com.pheonix.orchestrationengine.dto;

public class CustomerAnalyticsDto {
    private String companyName;
    private double totalCost;
    private double currentMonthCost;
    private double cpuAvg;
    private double memAvg;

    public CustomerAnalyticsDto() {}

    public CustomerAnalyticsDto(String companyName, double totalCost, double currentMonthCost, double cpuAvg, double memAvg) {
        this.companyName = companyName;
        this.totalCost = totalCost;
        this.currentMonthCost = currentMonthCost;
        this.cpuAvg = cpuAvg;
        this.memAvg = memAvg;
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
}
