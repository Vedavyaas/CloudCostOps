//package com.pheonix.orchestrationengine.config;
//
//import com.pheonix.orchestrationengine.repository.*;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//import java.time.YearMonth;
//import java.time.ZoneOffset;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class DataSeeder implements CommandLineRunner {
//
//    // ── Agent profiles [prod-agent-1, prod-agent-2, dev-agent-1, staging-agent] ──
//    private static final String[] AGENT_IDS  = {"prod-agent-1", "prod-agent-2", "dev-agent-1", "staging-agent"};
//    private static final double[] BASE_CPU   = {45.0,  62.0,  18.0,  32.0};
//    private static final double[] BASE_MEM   = {62.0,  74.0,  30.0,  46.0};
//    private static final int[]    CPU_CORES  = {16,    32,     8,    16};
//    private static final long[]   TOTAL_MB   = {32768, 65536, 16384, 32768};
//    private static final int[]    BASE_RPM   = {1800,  3200,   200,   850};
//    private static final int[]    BASE_QPS   = { 420,   650,    80,   270};
//    private static final double[] STORAGE_GB = { 380,   480,    80,   200};
//
//    private final CloudMetricRepository cloudMetricRepository;
//
//    public DataSeeder(CloudMetricRepository cloudMetricRepository) {
//        this.cloudMetricRepository = cloudMetricRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        if (!cloudMetricRepository.findAllByCompanyInfoEntity_CompanyName("amazon").isEmpty()) {
//            System.out.println("[DataSeeder] Data already present — skipping seed.");
//            return;
//        }
//
//        System.out.println("[DataSeeder] Seeding 240 static cloud metric records...");
//
//        String[][] companies = {
//            {"amazon", "amz", "10.0"},
//            {"vercel", "vcl", "10.1"}
//        };
//        int[][] yearMonths = {{2026, 4}, {2026, 5}, {2026, 6}};
//        int recordsPerMonth = 40;
//
//        List<CloudMetricEntity> all = new ArrayList<>();
//        for (String[] co : companies) {
//            for (int[] ym : yearMonths) {
//                all.addAll(generateMonth(co[0], co[1], co[2], ym[0], ym[1], recordsPerMonth));
//            }
//        }
//
//        cloudMetricRepository.saveAll(all);
//        System.out.printf(
//            "[DataSeeder] Done — inserted %d records (%d companies × 3 months × %d records).%n",
//            all.size(), companies.length, recordsPerMonth);
//    }
//
//    private List<CloudMetricEntity> generateMonth(
//            String company, String prefix, String ipNet,
//            int year, int month, int count) {
//
//        List<CloudMetricEntity> records = new ArrayList<>(count);
//        YearMonth ym = YearMonth.of(year, month);
//        int daysInMonth = ym.lengthOfMonth();
//
//        // Per-company IP subnet shift: amazon=0, vercel=1 already encoded in ipNet
//        String[] agentIps = {
//            ipNet + ".0.10",
//            ipNet + ".0.11",
//            ipNet + ".0.20",
//            ipNet + ".0.30"
//        };
//
//        for (int i = 0; i < count; i++) {
//            int agentIdx = i % 4;
//
//            // ── Timestamp: spread evenly across month ─────────────────────
//            int dayOfMonth = 1 + (i * daysInMonth / count);
//            int hour       = (i * 23 / count);
//            Instant ts = ym.atDay(dayOfMonth)
//                           .atTime(hour, (i * 7) % 60)
//                           .toInstant(ZoneOffset.UTC);
//
//            // ── Deterministic oscillation — same formula as seed_data.py ──
//            double wave  = Math.sin(i * Math.PI / 10.0);  // -1 .. +1
//            double wave2 = Math.cos(i * Math.PI / 8.0);   // secondary oscillation
//
//            double cpu    = clamp(BASE_CPU[agentIdx]  + 15.0 * wave,  5.0, 95.0);
//            double memPct = clamp(BASE_MEM[agentIdx]  + 10.0 * wave2, 10.0, 95.0);
//
//            long totalMb = TOTAL_MB[agentIdx];
//            long usedMb  = (long) (totalMb * memPct / 100.0);
//            long freeMb  = totalMb - usedMb;
//
//            double netIn  = clamp(50.0 + 120.0 * ((cpu - 5.0) / 90.0), 10.0, 350.0);
//            double netOut = netIn * 0.78;
//            double diskPct   = clamp(28.0 + 22.0 * wave,  8.0, 90.0);
//            double storageGb = STORAGE_GB[agentIdx] + month * 8.0 + i * 1.5;
//            double diskRead  = clamp(40.0 + 90.0 * ((cpu - 5.0) / 90.0), 5.0, 400.0);
//            double diskWrite = diskRead * 0.62;
//
//            int    qps     = (int) clamp(BASE_QPS[agentIdx] * (0.75 + 0.5 * ((cpu - 5.0) / 90.0)), 5, 2000);
//            double queryMs = clamp(4.0 + 22.0 * (1.0 - (cpu - 5.0) / 90.0), 0.5, 200.0);
//            double cacheHit = clamp(0.95 - 0.06 * wave, 0.82, 0.999);
//            int    dbConns  = Math.max(1, qps / 8);
//            double dbSizeGb = STORAGE_GB[agentIdx] * 0.4 + month * 3.0 + i * 0.8;
//
//            int    rpm    = (int) clamp(BASE_RPM[agentIdx] * (0.75 + 0.5 * ((cpu - 5.0) / 90.0)), 10, 8000);
//            double errPct = clamp(0.4 + 2.5 * Math.max(0, wave), 0.05, 8.0);
//            double respMs = clamp(40.0 + 220.0 * (1.0 - (cpu - 5.0) / 90.0), 15.0, 900.0);
//
//            // ── Build entity ───────────────────────────────────────────────
//            String eventId = String.format("evt-%s-%d-%02d-%02d", prefix, year, month, i + 1);
//
//            CloudMetricEntity e = new CloudMetricEntity();
//            e.setEventId(eventId);
//            e.setLocalDateTime(ts);
//            e.setCompanyInfoEntity(new CompanyInfoEntity(company));
//            e.setAgentInfoEntity(new AgentInfoEntity(agentId(agentIdx), agentId(agentIdx) + ".internal", agentIps[agentIdx]));
//            e.setComputeInfoEntity(new ComputeInfoEntity(
//                    round2(cpu), CPU_CORES[agentIdx],
//                    round2(cpu / 10.0), round2(cpu / 12.0), round2(cpu / 15.0)));
//            e.setMemoryInfoEntity(new MemoryInfoEntity(totalMb, usedMb, freeMb, round2(memPct)));
//            e.setNetworkInfoEntity(new NetworkInfoEntity(round2(netIn), round2(netOut), (int)(netIn * 0.4)));
//            e.setDiskInfoEntity(new DiskInfoEntity(round2(diskRead), round2(diskWrite), round2(diskPct), round2(storageGb)));
//            e.setDatabaseInfoEntity(new DatabaseInfoEntity(dbConns, qps, round2(queryMs), round4(cacheHit), round2(dbSizeGb)));
//            e.setApplicationInfoEntity(new ApplicationInfoEntity(rpm, round3(errPct), round2(respMs)));
//            e.setResourceInfoEntity(new ResourceInfoEntity(
//                    "EC2", "res-" + eventId, "PRODUCTION", "ap-south-1", "ap-south-1a"));
//            // No audit metric — produced by audit-ml-service via Kafka
//            records.add(e);
//        }
//        return records;
//    }
//
//    private static String agentId(int idx) { return AGENT_IDS[idx]; }
//
//    private static double clamp(double v, double min, double max) {
//        return Math.max(min, Math.min(max, v));
//    }
//
//    private static double round2(double v) { return Math.round(v * 100.0) / 100.0; }
//    private static double round3(double v) { return Math.round(v * 1000.0) / 1000.0; }
//    private static double round4(double v) { return Math.round(v * 10000.0) / 10000.0; }
//}
