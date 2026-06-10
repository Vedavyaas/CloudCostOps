package com.pheonix.orchestrationengine.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ComputeInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double cpuUsagePercent;
    private int cpuCores;
    private double loadAverage1m;
    private double loadAverage5m;
    private double loadAverage15m;

    public ComputeInfoEntity() {
    }

    public ComputeInfoEntity(double cpuUsagePercent, int cpuCores, double loadAverage1m, double loadAverage5m, double loadAverage15m) {
        this.cpuUsagePercent = cpuUsagePercent;
        this.cpuCores = cpuCores;
        this.loadAverage1m = loadAverage1m;
        this.loadAverage5m = loadAverage5m;
        this.loadAverage15m = loadAverage15m;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public void setCpuUsagePercent(double cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public double getLoadAverage1m() {
        return loadAverage1m;
    }

    public void setLoadAverage1m(double loadAverage1m) {
        this.loadAverage1m = loadAverage1m;
    }

    public double getLoadAverage5m() {
        return loadAverage5m;
    }

    public void setLoadAverage5m(double loadAverage5m) {
        this.loadAverage5m = loadAverage5m;
    }

    public double getLoadAverage15m() {
        return loadAverage15m;
    }

    public void setLoadAverage15m(double loadAverage15m) {
        this.loadAverage15m = loadAverage15m;
    }
}
