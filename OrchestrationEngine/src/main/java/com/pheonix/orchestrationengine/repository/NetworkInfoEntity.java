package com.pheonix.orchestrationengine.repository;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class NetworkInfoEntity {
    private Long id;

    private double networkInMB;
    private double networkOutMB;
    private int activeConnections;

    public NetworkInfoEntity() {
    }

    public NetworkInfoEntity(double networkInMB, double networkOutMB, int activeConnections) {
        this.networkInMB = networkInMB;
        this.networkOutMB = networkOutMB;
        this.activeConnections = activeConnections;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public double getNetworkInMB() {
        return networkInMB;
    }

    public void setNetworkInMB(double networkInMB) {
        this.networkInMB = networkInMB;
    }

    public double getNetworkOutMB() {
        return networkOutMB;
    }

    public void setNetworkOutMB(double networkOutMB) {
        this.networkOutMB = networkOutMB;
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    public void setActiveConnections(int activeConnections) {
        this.activeConnections = activeConnections;
    }
}
