package com.pheonix.orchestrationengine.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CloudAuditMetricRepository extends MongoRepository<CloudAuditMetricEntity, String> {
    CloudAuditMetricEntity findByEventId(String eventId);
}
