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
public class CloudAuditMetricRepository {

    private final DynamoDbTable<CloudAuditMetricEntity> table;

    public CloudAuditMetricRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("cloud_audit_metrics", TableSchema.fromBean(CloudAuditMetricEntity.class));
    }

    public CloudAuditMetricEntity save(CloudAuditMetricEntity entity) {
        table.putItem(entity);
        return entity;
    }

    public CloudAuditMetricEntity findById(String id) {
        return table.getItem(Key.builder().partitionValue(id).build());
    }

    public CloudAuditMetricEntity findByEventId(String eventId) {
        return table.index("eventId-index")
                .query(QueryConditional.keyEqualTo(Key.builder().partitionValue(eventId).build()))
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .orElse(null);
    }

    public List<CloudAuditMetricEntity> findAll() {
        return table.scan().items().stream().collect(Collectors.toList());
    }
    
    public void deleteById(String id) {
        table.deleteItem(Key.builder().partitionValue(id).build());
    }
}
