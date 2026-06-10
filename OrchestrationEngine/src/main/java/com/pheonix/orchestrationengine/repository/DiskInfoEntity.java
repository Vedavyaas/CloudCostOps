package com.pheonix.orchestrationengine.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class DiskInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double diskReadMB;
    private double diskWriteMB;
    private double diskUsagePercent;
    private double storageUsedGB;

    public DiskInfoEntity() {
    }

    public DiskInfoEntity(double diskReadMB, double diskWriteMB, double diskUsagePercent, double storageUsedGB) {
        this.diskReadMB = diskReadMB;
        this.diskWriteMB = diskWriteMB;
        this.diskUsagePercent = diskUsagePercent;
        this.storageUsedGB = storageUsedGB;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public double getDiskReadMB() {
        return diskReadMB;
    }

    public void setDiskReadMB(double diskReadMB) {
        this.diskReadMB = diskReadMB;
    }

    public double getDiskWriteMB() {
        return diskWriteMB;
    }

    public void setDiskWriteMB(double diskWriteMB) {
        this.diskWriteMB = diskWriteMB;
    }

    public double getDiskUsagePercent() {
        return diskUsagePercent;
    }

    public void setDiskUsagePercent(double diskUsagePercent) {
        this.diskUsagePercent = diskUsagePercent;
    }

    public double getStorageUsedGB() {
        return storageUsedGB;
    }

    public void setStorageUsedGB(double storageUsedGB) {
        this.storageUsedGB = storageUsedGB;
    }
}
