package com.pheonix.orchestrationengine.dto;

public class CostWeightsDto {
    private double cpuCostPerPercent  = 0.05;   // $ per 1% CPU usage per sample
    private double memCostPerGB       = 0.01;   // $ per GB memory used per sample
    private double diskCostPerGB      = 0.008;  // $ per GB storage used per sample
    private double networkCostPerMB   = 0.002;  // $ per MB network (in+out) per sample

    public CostWeightsDto() {}

    public CostWeightsDto(double cpuCostPerPercent, double memCostPerGB, double diskCostPerGB, double networkCostPerMB) {
        this.cpuCostPerPercent = cpuCostPerPercent;
        this.memCostPerGB      = memCostPerGB;
        this.diskCostPerGB     = diskCostPerGB;
        this.networkCostPerMB  = networkCostPerMB;
    }

    public double getCpuCostPerPercent()  { return cpuCostPerPercent; }
    public double getMemCostPerGB()       { return memCostPerGB; }
    public double getDiskCostPerGB()      { return diskCostPerGB; }
    public double getNetworkCostPerMB()   { return networkCostPerMB; }

    public void setCpuCostPerPercent(double v)  { this.cpuCostPerPercent = v; }
    public void setMemCostPerGB(double v)       { this.memCostPerGB = v; }
    public void setDiskCostPerGB(double v)      { this.diskCostPerGB = v; }
    public void setNetworkCostPerMB(double v)   { this.networkCostPerMB = v; }
}
