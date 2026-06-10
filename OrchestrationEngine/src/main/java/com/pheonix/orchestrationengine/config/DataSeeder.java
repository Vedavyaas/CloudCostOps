package com.pheonix.orchestrationengine.config;

import com.pheonix.orchestrationengine.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CloudMetricRepository cloudMetricRepository;

    public DataSeeder(CloudMetricRepository cloudMetricRepository) {
        this.cloudMetricRepository = cloudMetricRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // ── 1. Wipe everything so only amazon & vercel remain ──────────────
        System.out.println("[DataSeeder] Clearing all existing metric data...");
        cloudMetricRepository.deleteAll();
        System.out.println("[DataSeeder] All records deleted.");

        // ── 2. Seed amazon and vercel ──────────────────────────────────────
        String[] companies = { "amazon", "vercel" };

        for (String company : companies) {
            System.out.printf("[DataSeeder] Seeding %s...%n", company);
            long t0 = System.currentTimeMillis();

            int total   = 2000;
            int threads = Math.max(2, Runtime.getRuntime().availableProcessors());
            ExecutorService exec = Executors.newFixedThreadPool(threads);
            int batchSize = total / threads;

            List<Future<List<CloudMetricEntity>>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                final int size = (i == threads - 1) ? (total - i * batchSize) : batchSize;
                futures.add(exec.submit(() -> buildBatch(company, size)));
            }

            List<CloudMetricEntity> all = new ArrayList<>(total);
            for (Future<List<CloudMetricEntity>> f : futures) all.addAll(f.get());
            exec.shutdown();

            all.sort((a, b) -> a.getLocalDateTime().compareTo(b.getLocalDateTime()));
            cloudMetricRepository.saveAll(all);

            System.out.printf("[DataSeeder] ✓ Seeded %d records for '%s' in %dms%n",
                    all.size(), company, System.currentTimeMillis() - t0);
        }

        System.out.println("[DataSeeder] Done.");
    }

    // ── Batch builder ──────────────────────────────────────────────────────
    private List<CloudMetricEntity> buildBatch(String company, int size) {
        Random rng = new Random();
        Instant now = Instant.now();
        List<CloudMetricEntity> batch = new ArrayList<>(size);

        for (int j = 0; j < size; j++) {
            CloudMetricEntity e = new CloudMetricEntity();
            e.setEventId(UUID.randomUUID().toString());
            // Spread across 90 days so monthly breakdown is meaningful
            e.setLocalDateTime(now.minus(rng.nextInt(90 * 24 * 60), ChronoUnit.MINUTES));
            e.setCompanyInfoEntity(new CompanyInfoEntity(company));

            // Compute
            double cpu = 5 + rng.nextDouble() * 90;
            e.setComputeInfoEntity(new ComputeInfoEntity(
                    cpu, 16 + rng.nextInt(16),
                    cpu / 10.0, cpu / 12.0, cpu / 15.0));

            // Memory
            long totalMb  = 32768;
            double memPct = 15 + rng.nextDouble() * 80;
            long usedMb   = (long) (totalMb * memPct / 100);
            e.setMemoryInfoEntity(new MemoryInfoEntity(totalMb, usedMb, totalMb - usedMb, memPct));

            // Disk
            double diskRead  = 5  + rng.nextDouble() * 300;
            double diskWrite = 2  + rng.nextDouble() * 200;
            double diskPct   = 10 + rng.nextDouble() * 75;
            double storage   = 20 + rng.nextDouble() * 980;
            e.setDiskInfoEntity(new DiskInfoEntity(diskRead, diskWrite, diskPct, storage));

            // Network
            double netIn  = 0.5 + rng.nextDouble() * 150;
            double netOut = 0.5 + rng.nextDouble() * 120;
            int    conns  = 5   + rng.nextInt(1995);
            e.setNetworkInfoEntity(new NetworkInfoEntity(netIn, netOut, conns));

            // Application
            int    rpm    = 10  + rng.nextInt(4990);
            double errPct = rng.nextDouble() * 8;
            double respMs = 10  + rng.nextDouble() * 990;
            e.setApplicationInfoEntity(new ApplicationInfoEntity(rpm, errPct, respMs));

            // Database
            int    dbConns  = 1   + rng.nextInt(499);
            int    qps      = 5   + rng.nextInt(995);
            double queryMs  = 0.5 + rng.nextDouble() * 199;
            double cacheHit = 0.5 + rng.nextDouble() * 0.49;
            double dbSizeGB = 0.5 + rng.nextDouble() * 199;
            e.setDatabaseInfoEntity(new DatabaseInfoEntity(dbConns, qps, queryMs, cacheHit, dbSizeGB));

            String[] agentIds = { "prod-agent-1", "prod-agent-2", "dev-agent-1", "staging-agent" };
            String agentId = agentIds[rng.nextInt(agentIds.length)];
            e.setAgentInfoEntity(new AgentInfoEntity(agentId, agentId + ".internal", "10.0.0." + rng.nextInt(255)));
            e.setResourceInfoEntity(new ResourceInfoEntity());

            CloudAuditMetricEntity audit = new CloudAuditMetricEntity();
            audit.setEventId(e.getEventId());
            audit.setAuditTimestamp(e.getLocalDateTime());
            audit.setTotalEstimatedCost(10 + rng.nextDouble() * 50);
            audit.setDailyCost(1 + rng.nextDouble() * 5);
            audit.setAverageCpuUsage(cpu);
            audit.setPeakCpuUsage(Math.min(100, cpu + 10));
            audit.setAverageMemoryUsage(memPct);
            audit.setPeakMemoryUsage(Math.min(100, memPct + 10));
            audit.setAverageDiskUsage(diskPct);
            audit.setAverageNetworkUsage(netIn);
            audit.setAverageResponseTime(respMs);
            audit.setErrorTrend(errPct);
            audit.setAverageQPS(qps);
            audit.setPeakQPS(qps * 1.5);
            audit.setCacheEfficiencyScore(cacheHit * 100);
            audit.setComputeCostPercentage(40);
            audit.setMemoryCostPercentage(30);
            audit.setNetworkCostPercentage(10);
            audit.setStorageCostPercentage(20);
            e.setCloudAuditMetricEntity(audit);

            batch.add(e);
        }
        return batch;
    }
}
