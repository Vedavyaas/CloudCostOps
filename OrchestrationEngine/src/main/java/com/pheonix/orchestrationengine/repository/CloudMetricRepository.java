package com.pheonix.orchestrationengine.repository;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CloudMetricRepository {

    private final DynamoDbTable<CloudMetricEntity> table;

    public CloudMetricRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("cloud_metrics", TableSchema.fromBean(CloudMetricEntity.class));
    }

    public CloudMetricEntity save(CloudMetricEntity entity) {
        table.putItem(entity);
        return entity;
    }

    public void saveAll(List<CloudMetricEntity> entities) {
        for (CloudMetricEntity entity : entities) {
            table.putItem(entity);
        }
    }

    public CloudMetricEntity findById(String id) {
        return table.getItem(Key.builder().partitionValue(id).build());
    }

    public CloudMetricEntity findByEventId(String eventId) {
        return table.getItem(Key.builder().partitionValue(eventId).build());
    }

    public List<CloudMetricEntity> findAllByCompanyInfoEntity_CompanyName(String companyName) {
        return table.index("companyName-index")
                .query(QueryConditional.keyEqualTo(Key.builder().partitionValue(companyName).build()))
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    public List<CloudMetricEntity> findAll() {
        return table.scan().items().stream().collect(Collectors.toList());
    }
    
    public void deleteById(String id) {
        table.deleteItem(Key.builder().partitionValue(id).build());
    }

    public long count() {
        return table.scan().items().stream().count();
    }
}
