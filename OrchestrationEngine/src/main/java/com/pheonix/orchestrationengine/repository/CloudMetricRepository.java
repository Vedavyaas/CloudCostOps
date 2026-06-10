package com.pheonix.orchestrationengine.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CloudMetricRepository extends JpaRepository<CloudMetricEntity, Long> {
    CloudMetricEntity findByEventId(String eventId);

    List<CloudMetricEntity> findAllByCompanyInfoEntity_CompanyName(String companyInfoEntityCompanyName);
}
