package com.pheonix.orchestrationengine.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.Instant;

@Document(collection = "cloud_metrics")
public class CloudMetricEntity {
    @Id
    private String id;

    @Indexed
    private String eventId;

    private Instant localDateTime;

    private CompanyInfoEntity companyInfoEntity;
    private ComputeInfoEntity computeInfoEntity;
    private AgentInfoEntity agentInfoEntity;
    private ResourceInfoEntity resourceInfoEntity;
    private MemoryInfoEntity memoryInfoEntity;
    private NetworkInfoEntity networkInfoEntity;
    private DiskInfoEntity diskInfoEntity;
    private DatabaseInfoEntity databaseInfoEntity;
    private ApplicationInfoEntity applicationInfoEntity;
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

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
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
