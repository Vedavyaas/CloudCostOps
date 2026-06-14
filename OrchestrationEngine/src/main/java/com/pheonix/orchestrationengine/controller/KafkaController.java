//package com.pheonix.orchestrationengine.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.pheonix.orchestrationengine.input_metrics.CloudMetricEvent;
//import com.pheonix.orchestrationengine.input_metrics.CloudAuditMetricEvent;
//import com.pheonix.orchestrationengine.repository.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//
//@Component
//public class KafkaController {
//    private static final Logger log = LoggerFactory.getLogger(KafkaController.class);
//
//    private final ObjectMapper objectMapper;
//    private final CloudMetricRepository cloudMetricRepository;
//    private final CloudAuditMetricRepository cloudAuditMetricRepository;
//
//    public KafkaController(
//            ObjectMapper objectMapper,
//            CloudMetricRepository cloudMetricRepository,
//            CloudAuditMetricRepository cloudAuditMetricRepository
//    ) {
//        this.objectMapper = objectMapper;
//        this.cloudMetricRepository = cloudMetricRepository;
//        this.cloudAuditMetricRepository = cloudAuditMetricRepository;
//    }
//
//    /*
//    TOPIC NAME : cloud_metrics
//    {
//      "eventId": "1",
//
//      "company": {
//        "companyName": "Acme Corp"
//      },
//
//      "agent": {
//        "agentId": "agent-01",
//        "hostname": "prod-server-01",
//        "ipAddress": "10.0.1.25"
//      },
//
//      "resource": {
//        "resourceType": "POSTGRESQL",
//        "resourceId": "db-prod-01",
//        "environment": "PRODUCTION",
//        "region": "ap-south-1",
//        "availabilityZone": "ap-south-1a"
//      },
//
//      "compute": {
//        "cpuUsagePercent": 42.7,
//        "cpuCores": 8,
//        "loadAverage1m": 1.8,
//        "loadAverage5m": 1.4,
//        "loadAverage15m": 1.2
//      },
//
//      "memory": {
//        "totalMB": 16384,
//        "usedMB": 9216,
//        "freeMB": 7168,
//        "usagePercent": 56.2
//      },
//
//      "network": {
//        "networkInMB": 245.76,
//        "networkOutMB": 512.00,
//        "activeConnections": 67
//      },
//
//      "disk": {
//        "diskReadMB": 128.4,
//        "diskWriteMB": 84.2,
//        "diskUsagePercent": 48.3,
//        "storageUsedGB": 380.5
//      },
//
//      "database": {
//        "activeConnections": 54,
//        "queriesPerSecond": 420,
//        "averageQueryTimeMs": 12.4,
//        "cacheHitRatio": 96.8,
//        "databaseSizeGB": 156.4
//      },
//
//      "application": {
//        "requestsPerMinute": 1850,
//        "errorRatePercent": 0.7,
//        "responseTimeMs": 120
//      }
//    }
//    */
//
//    @KafkaListener(topics = "cloud_metrics", groupId = "orc_group")
//    public void listenCloudMetrics(String payload) {
//        try {
//            CloudMetricEvent event = objectMapper.readValue(payload, CloudMetricEvent.class);
//            log.info("Successfully converted payload to CloudMetricEvent: {}", event);
//
//            // ── Idempotency: reuse existing record if DataSeeder already inserted it ──
//            CloudMetricEntity saved = cloudMetricRepository.findByEventId(event.eventId());
//            if (saved != null) {
//                log.info("CloudMetricEntity already exists for eventId={} — skipping duplicate insert", event.eventId());
//                // Still link audit if it arrived first and is not yet attached
//                if (saved.getCloudAuditMetricEntity() == null) {
//                    CloudAuditMetricEntity existingAudit = cloudAuditMetricRepository.findByEventId(event.eventId());
//                    if (existingAudit != null) {
//                        saved.setCloudAuditMetricEntity(existingAudit);
//                        cloudMetricRepository.save(saved);
//                        log.info("Linked pre-existing audit metric for eventId={}", event.eventId());
//                    }
//                }
//                return;
//            }
//
//            // ── New record: build and persist ────────────────────────────────────────
//            Instant localDateTime = Instant.now();
//
//            CompanyInfoEntity companyInfoEntity = null;
//            if (event.company() != null) {
//                companyInfoEntity = new CompanyInfoEntity(event.company().companyName());
//            }
//            AgentInfoEntity agentInfoEntity = null;
//            if (event.agent() != null) {
//                agentInfoEntity = new AgentInfoEntity(
//                        event.agent().agentId(), event.agent().hostname(), event.agent().ipAddress());
//            }
//            ComputeInfoEntity computeInfoEntity = null;
//            if (event.compute() != null) {
//                computeInfoEntity = new ComputeInfoEntity(
//                        event.compute().cpuUsagePercent(), event.compute().cpuCores(),
//                        event.compute().loadAverage1m(), event.compute().loadAverage5m(),
//                        event.compute().loadAverage15m());
//            }
//            ResourceInfoEntity resourceInfoEntity = null;
//            if (event.resource() != null) {
//                resourceInfoEntity = new ResourceInfoEntity(
//                        event.resource().resourceType(), event.resource().resourceId(),
//                        event.resource().environment(), event.resource().region(),
//                        event.resource().availabilityZone());
//            }
//            MemoryInfoEntity memoryInfoEntity = null;
//            if (event.memory() != null) {
//                memoryInfoEntity = new MemoryInfoEntity(
//                        event.memory().totalMB(), event.memory().usedMB(),
//                        event.memory().freeMB(), event.memory().usagePercent());
//            }
//            NetworkInfoEntity networkInfoEntity = null;
//            if (event.network() != null) {
//                networkInfoEntity = new NetworkInfoEntity(
//                        event.network().networkInMB(), event.network().networkOutMB(),
//                        event.network().activeConnections());
//            }
//            DiskInfoEntity diskInfoEntity = null;
//            if (event.disk() != null) {
//                diskInfoEntity = new DiskInfoEntity(
//                        event.disk().diskReadMB(), event.disk().diskWriteMB(),
//                        event.disk().diskUsagePercent(), event.disk().storageUsedGB());
//            }
//            DatabaseInfoEntity databaseInfoEntity = null;
//            if (event.database() != null) {
//                databaseInfoEntity = new DatabaseInfoEntity(
//                        event.database().activeConnections(), event.database().queriesPerSecond(),
//                        event.database().averageQueryTimeMs(), event.database().cacheHitRatio(),
//                        event.database().databaseSizeGB());
//            }
//            ApplicationInfoEntity applicationInfoEntity = null;
//            if (event.application() != null) {
//                applicationInfoEntity = new ApplicationInfoEntity(
//                        event.application().requestsPerMinute(), event.application().errorRatePercent(),
//                        event.application().responseTimeMs());
//            }
//
//            CloudMetricEntity cloudMetricEntity = new CloudMetricEntity(
//                    event.eventId(), localDateTime, companyInfoEntity, computeInfoEntity,
//                    agentInfoEntity, resourceInfoEntity, memoryInfoEntity, networkInfoEntity,
//                    diskInfoEntity, databaseInfoEntity, applicationInfoEntity);
//
//
//            saved = cloudMetricRepository.save(cloudMetricEntity);
//            log.info("Saved new CloudMetricEntity eventId={}", event.eventId());
//
//            // Link audit if it already arrived
//            CloudAuditMetricEntity existingAudit = cloudAuditMetricRepository.findByEventId(event.eventId());
//            if (existingAudit != null) {
//                saved.setCloudAuditMetricEntity(existingAudit);
//                cloudMetricRepository.save(saved);
//                log.info("Linked pre-existing audit metric for eventId={}", event.eventId());
//            }
//
//        } catch (JsonProcessingException e) {
//            log.error("Failed to parse Kafka message payload: {}", payload, e);
//        }
//    }
//
//    /*
//     * Topic: cloud_audit_metric
//     *
//     * All analytics fields in this payload are produced by the audit-ml-service
//     * (or an upstream monitoring agent) before being published to Kafka.
//     * This listener only deserializes the event and persists it — no recalculation
//     * or derivation of values is performed here.
//     *
//     * Cost breakdowns (computeCostPercentage, memoryCostPercentage, etc.),
//     * utilization scores (resourceUtilizationScore, queryEfficiencyScore, etc.),
//     * anomaly scores, and capacity projections all originate from the producer side.
//     *
//     * Example payload:
//     * {
//     *   "eventId": "evt-audit-101",
//     *   "auditTimestamp": "2026-05-21T14:32:45Z",
//     *   "totalEstimatedCost": 1250.50,
//     *   "computeCostPercentage": 45.0,
//     *   "memoryCostPercentage": 25.0,
//     *   "networkCostPercentage": 10.0,
//     *   "storageCostPercentage": 20.0,
//     *   "costPerRequest": 0.0005,
//     *   "costPerQuery": 0.002,
//     *   "dailyCost": 300.0,
//     *   "weeklyCost": 2100.0,
//     *   "monthlyCost": 9000.0,
//     *   "costGrowthRate": 1.2,
//     *   "resourceUtilizationScore": 68.5,
//     *   "averageCpuUsage": 42.7,
//     *   "peakCpuUsage": 85.0,
//     *   "averageMemoryUsage": 56.2,
//     *   "peakMemoryUsage": 78.0,
//     *   "averageDiskUsage": 48.3,
//     *   "averageNetworkUsage": 245.76,
//     *   "connectionUtilization": 0.54,
//     *   "queryEfficiencyScore": 33.87,
//     *   "averageQPS": 420.0,
//     *   "peakQPS": 600.0,
//     *   "databaseGrowthRate": 0.8,
//     *   "cacheEfficiencyScore": 96.8,
//     *   "availabilityScore": 99.9,
//     *   "errorTrend": 0.05,
//     *   "requestGrowthRate": 2.5,
//     *   "averageResponseTime": 120.0,
//     *   "peakResponseTime": 450.0,
//     *   "applicationHealthScore": 98.5,
//     *   "daysUntilStorageFull": 45,
//     *   "projectedMonthlyTraffic": 5000000.0,
//     *   "projectedMonthlyCost": 9200.0,
//     *   "cpuGrowthRate": 0.5,
//     *   "memoryGrowthRate": 0.3,
//     *   "cpuAnomalyScore": 0.12,
//     *   "memoryAnomalyScore": 0.05,
//     *   "networkAnomalyScore": 0.22,
//     *   "databaseAnomalyScore": 0.15,
//     *   "responseTimeAnomalyScore": 0.08,
//     *   "overallAnomalyScore": 0.13
//     * }
//     */
//
//    @KafkaListener(topics = "cloud_audit_metric", groupId = "orc_group")
//    public void listenAuditMetric(String payload) {
//        try {
//            CloudAuditMetricEvent event = objectMapper.readValue(payload, CloudAuditMetricEvent.class);
//            log.info("Successfully converted payload to CloudAuditMetricEvent: {}", event);
//
//            Instant auditTimestamp;
//            if (event.auditTimestamp() != null) {
//                try {
//                    auditTimestamp = Instant.parse(event.auditTimestamp());
//                } catch (Exception e) {
//                    log.error("Failed to parse audit timestamp: {}", event.auditTimestamp(), e);
//                    auditTimestamp = Instant.now();
//                }
//            } else {
//                auditTimestamp = Instant.now();
//            }
//
//            CloudAuditMetricEntity entity = new CloudAuditMetricEntity(
//                    event.eventId(),
//                    auditTimestamp,
//                    event.totalEstimatedCost(),
//                    event.computeCostPercentage(),
//                    event.memoryCostPercentage(),
//                    event.networkCostPercentage(),
//                    event.storageCostPercentage(),
//                    event.costPerRequest(),
//                    event.costPerQuery(),
//                    event.dailyCost(),
//                    event.weeklyCost(),
//                    event.monthlyCost(),
//                    event.costGrowthRate(),
//                    event.resourceUtilizationScore(),
//                    event.averageCpuUsage(),
//                    event.peakCpuUsage(),
//                    event.averageMemoryUsage(),
//                    event.peakMemoryUsage(),
//                    event.averageDiskUsage(),
//                    event.averageNetworkUsage(),
//                    event.connectionUtilization(),
//                    event.queryEfficiencyScore(),
//                    event.averageQPS(),
//                    event.peakQPS(),
//                    event.databaseGrowthRate(),
//                    event.cacheEfficiencyScore(),
//                    event.availabilityScore(),
//                    event.errorTrend(),
//                    event.requestGrowthRate(),
//                    event.averageResponseTime(),
//                    event.peakResponseTime(),
//                    event.applicationHealthScore(),
//                    event.daysUntilStorageFull(),
//                    event.projectedMonthlyTraffic(),
//                    event.projectedMonthlyCost(),
//                    event.cpuGrowthRate(),
//                    event.memoryGrowthRate(),
//                    event.cpuAnomalyScore(),
//                    event.memoryAnomalyScore(),
//                    event.networkAnomalyScore(),
//                    event.databaseAnomalyScore(),
//                    event.responseTimeAnomalyScore(),
//                    event.overallAnomalyScore()
//            );
//            CloudMetricEntity cloudMetricEntity = cloudMetricRepository.findByEventId(event.eventId());
//            if (cloudMetricEntity != null) {
//                // 1. Map to the specific ID in the cloud metric
//                entity.setId(cloudMetricEntity.getEventId());
//
//                // 2. Put it in the audit table
//                CloudAuditMetricEntity saved = cloudAuditMetricRepository.save(entity);
//
//                // 3. Update the cloud metric with the mapped audit
//                cloudMetricEntity.setCloudAuditMetricEntity(saved);
//                cloudMetricRepository.save(cloudMetricEntity);
//
//                log.info("Saved to audit table and linked to CloudMetricEntity with ID: {}", saved.getId());
//            } else {
//                log.warn(
//                        "CloudMetricEntity not yet available for eventId={}; audit discarded.",
//                        event.eventId()
//                );
//            }
//
//        } catch (JsonProcessingException e) {
//            log.error("Failed to parse Kafka message payload: {}", payload, e);
//        }
//    }
//}