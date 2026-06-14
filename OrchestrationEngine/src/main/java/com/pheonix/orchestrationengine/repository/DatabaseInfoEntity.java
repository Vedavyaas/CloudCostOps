package com.pheonix.orchestrationengine.repository;

public class DatabaseInfoEntity {
    private Long id;

    private int activeConnections;
    private int queriesPerSecond;
    private double averageQueryTimeMs;
    private double cacheHitRatio;
    private double databaseSizeGB;

    public DatabaseInfoEntity() {
    }

    public DatabaseInfoEntity(int activeConnections, int queriesPerSecond, double averageQueryTimeMs, double cacheHitRatio, double databaseSizeGB) {
        this.activeConnections = activeConnections;
        this.queriesPerSecond = queriesPerSecond;
        this.averageQueryTimeMs = averageQueryTimeMs;
        this.cacheHitRatio = cacheHitRatio;
        this.databaseSizeGB = databaseSizeGB;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    public void setActiveConnections(int activeConnections) {
        this.activeConnections = activeConnections;
    }

    public int getQueriesPerSecond() {
        return queriesPerSecond;
    }

    public void setQueriesPerSecond(int queriesPerSecond) {
        this.queriesPerSecond = queriesPerSecond;
    }

    public double getAverageQueryTimeMs() {
        return averageQueryTimeMs;
    }

    public void setAverageQueryTimeMs(double averageQueryTimeMs) {
        this.averageQueryTimeMs = averageQueryTimeMs;
    }

    public double getCacheHitRatio() {
        return cacheHitRatio;
    }

    public void setCacheHitRatio(double cacheHitRatio) {
        this.cacheHitRatio = cacheHitRatio;
    }

    public double getDatabaseSizeGB() {
        return databaseSizeGB;
    }

    public void setDatabaseSizeGB(double databaseSizeGB) {
        this.databaseSizeGB = databaseSizeGB;
    }
}
