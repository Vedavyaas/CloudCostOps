package com.pheonix.orchestrationengine.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MemoryInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private long totalMB;
    private long usedMB;
    private long freeMB;
    private double usagePercent;

    public MemoryInfoEntity() {
    }

    public MemoryInfoEntity(long totalMB, long usedMB, long freeMB, double usagePercent) {
        this.totalMB = totalMB;
        this.usedMB = usedMB;
        this.freeMB = freeMB;
        this.usagePercent = usagePercent;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public long getTotalMB() {
        return totalMB;
    }

    public void setTotalMB(long totalMB) {
        this.totalMB = totalMB;
    }

    public long getUsedMB() {
        return usedMB;
    }

    public void setUsedMB(long usedMB) {
        this.usedMB = usedMB;
    }

    public long getFreeMB() {
        return freeMB;
    }

    public void setFreeMB(long freeMB) {
        this.freeMB = freeMB;
    }

    public double getUsagePercent() {
        return usagePercent;
    }

    public void setUsagePercent(double usagePercent) {
        this.usagePercent = usagePercent;
    }
}
