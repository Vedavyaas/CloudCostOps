package com.pheonix.orchestrationengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudAuditMetricRepository extends JpaRepository<CloudAuditMetricEntity, Long> {
    CloudAuditMetricEntity findByEventId(String eventId);
}
