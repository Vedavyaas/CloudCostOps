package com.pheonix.orchestrationengine.repository;

public class AgentInfoEntity {
    private Long id;

    private String agentId;
    private String hostName;
    private String ipAddress;

    public AgentInfoEntity() {
    }

    public AgentInfoEntity(String agentId, String hostName, String ipAddress) {
        this.agentId = agentId;
        this.hostName = hostName;
        this.ipAddress = ipAddress;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
