package com.pheonix.orchestrationengine.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CloudMetricRepository extends MongoRepository<CloudMetricEntity, String> {
    CloudMetricEntity findByEventId(String eventId);

    List<CloudMetricEntity> findAllByCompanyInfoEntity_CompanyName(String companyInfoEntityCompanyName);
}
