package com.pheonix.orchestrationengine.repository;

public class ResourceInfoEntity {
    private Long id;

    private String resourceType;
    private String resourceId;
    private String environment;
    private String region;
    private String availabilityZone;

    public ResourceInfoEntity() {
    }

    public ResourceInfoEntity(String resourceType, String resourceId, String environment, String region, String availabilityZone) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.environment = environment;
        this.region = region;
        this.availabilityZone = availabilityZone;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAvailabilityZone() {
        return availabilityZone;
    }

    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }
}
