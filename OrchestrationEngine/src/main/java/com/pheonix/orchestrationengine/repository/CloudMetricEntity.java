package com.pheonix.orchestrationengine.repository;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class CloudMetricEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String eventId;

    private Instant localDateTime;

    @OneToOne(cascade = CascadeType.ALL)
    private CompanyInfoEntity companyInfoEntity;

    @OneToOne(cascade = CascadeType.ALL)
    private ComputeInfoEntity computeInfoEntity;

    @OneToOne(cascade = CascadeType.ALL)
    private AgentInfoEntity agentInfoEntity;

    @OneToOne(cascade = CascadeType.ALL)
    private ResourceInfoEntity resourceInfoEntity;

    @OneToOne(cascade = CascadeType.ALL)
    private MemoryInfoEntity memoryInfoEntity;

    @OneToOne(cascade = CascadeType.ALL)
    private NetworkInfoEntity networkInfoEntity;

    @OneToOne(cascade = CascadeType.ALL)
    private DiskInfoEntity diskInfoEntity;

    @OneToOne(cascade = CascadeType.ALL)
    private DatabaseInfoEntity databaseInfoEntity;

    @OneToOne(cascade = CascadeType.ALL)
    private ApplicationInfoEntity applicationInfoEntity;

    @OneToOne(cascade = CascadeType.ALL)
    private CloudAuditMetricEntity cloudAuditMetricEntity;

    public CloudMetricEntity() {
    }

    public CloudMetricEntity(
            String eventId,
            Instant localDateTime,
            CompanyInfoEntity companyInfoEntity,
            ComputeInfoEntity computeInfoEntity,
            AgentInfoEntity agentInfoEntity,
            ResourceInfoEntity resourceInfoEntity,
            MemoryInfoEntity memoryInfoEntity,
            NetworkInfoEntity networkInfoEntity,
            DiskInfoEntity diskInfoEntity,
            DatabaseInfoEntity databaseInfoEntity,
            ApplicationInfoEntity applicationInfoEntity
    ) {
        this.eventId = eventId;
        this.localDateTime = localDateTime;
        this.companyInfoEntity = companyInfoEntity;
        this.computeInfoEntity = computeInfoEntity;
        this.agentInfoEntity = agentInfoEntity;
        this.resourceInfoEntity = resourceInfoEntity;
        this.memoryInfoEntity = memoryInfoEntity;
        this.networkInfoEntity = networkInfoEntity;
        this.diskInfoEntity = diskInfoEntity;
        this.databaseInfoEntity = databaseInfoEntity;
        this.applicationInfoEntity = applicationInfoEntity;
        this.cloudAuditMetricEntity = null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Instant getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(Instant localDateTime) {
        this.localDateTime = localDateTime;
    }

    public CompanyInfoEntity getCompanyInfoEntity() {
        return companyInfoEntity;
    }

    public void setCompanyInfoEntity(CompanyInfoEntity companyInfoEntity) {
        this.companyInfoEntity = companyInfoEntity;
    }

    public ComputeInfoEntity getComputeInfoEntity() {
        return computeInfoEntity;
    }

    public void setComputeInfoEntity(ComputeInfoEntity computeInfoEntity) {
        this.computeInfoEntity = computeInfoEntity;
    }

    public AgentInfoEntity getAgentInfoEntity() {
        return agentInfoEntity;
    }

    public void setAgentInfoEntity(AgentInfoEntity agentInfoEntity) {
        this.agentInfoEntity = agentInfoEntity;
    }

    public ResourceInfoEntity getResourceInfoEntity() {
        return resourceInfoEntity;
    }

    public void setResourceInfoEntity(ResourceInfoEntity resourceInfoEntity) {
        this.resourceInfoEntity = resourceInfoEntity;
    }

    public MemoryInfoEntity getMemoryInfoEntity() {
        return memoryInfoEntity;
    }

    public void setMemoryInfoEntity(MemoryInfoEntity memoryInfoEntity) {
        this.memoryInfoEntity = memoryInfoEntity;
    }

    public NetworkInfoEntity getNetworkInfoEntity() {
        return networkInfoEntity;
    }

    public void setNetworkInfoEntity(NetworkInfoEntity networkInfoEntity) {
        this.networkInfoEntity = networkInfoEntity;
    }

    public DiskInfoEntity getDiskInfoEntity() {
        return diskInfoEntity;
    }

    public void setDiskInfoEntity(DiskInfoEntity diskInfoEntity) {
        this.diskInfoEntity = diskInfoEntity;
    }

    public DatabaseInfoEntity getDatabaseInfoEntity() {
        return databaseInfoEntity;
    }

    public void setDatabaseInfoEntity(DatabaseInfoEntity databaseInfoEntity) {
        this.databaseInfoEntity = databaseInfoEntity;
    }

    public ApplicationInfoEntity getApplicationInfoEntity() {
        return applicationInfoEntity;
    }

    public void setApplicationInfoEntity(ApplicationInfoEntity applicationInfoEntity) {
        this.applicationInfoEntity = applicationInfoEntity;
    }

    public CloudAuditMetricEntity getCloudAuditMetricEntity() {
        return cloudAuditMetricEntity;
    }

    public void setCloudAuditMetricEntity(CloudAuditMetricEntity cloudAuditMetricEntity) {
        this.cloudAuditMetricEntity = cloudAuditMetricEntity;
    }
}
