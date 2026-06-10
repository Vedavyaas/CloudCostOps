package com.pheonix.orchestrationengine.service;

import com.pheonix.orchestrationengine.dto.CompanyAnalyticsDto;
import com.pheonix.orchestrationengine.dto.CompanyAnalyticsDto.DailySnapshot;
import com.pheonix.orchestrationengine.dto.CompanyAnalyticsDto.MonthlySnapshot;
import com.pheonix.orchestrationengine.dto.CostWeightsDto;
import com.pheonix.orchestrationengine.repository.CloudAuditMetricEntity;
import com.pheonix.orchestrationengine.repository.CloudMetricEntity;
import com.pheonix.orchestrationengine.repository.CloudMetricRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private static final ZoneId UTC       = ZoneId.of("UTC");
    private static final DateTimeFormatter DAY_FMT   = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final CloudMetricRepository cloudMetricRepository;

    public AnalysisService(CloudMetricRepository cloudMetricRepository) {
        this.cloudMetricRepository = cloudMetricRepository;
    }

    public CompanyAnalyticsDto getCompanyAnalytics(String companyName, CostWeightsDto weights) {
        if (weights == null) weights = new CostWeightsDto();

        List<CloudMetricEntity> allMetrics =
                cloudMetricRepository.findAllByCompanyInfoEntity_CompanyName(companyName);

        return computeAnalytics(companyName, allMetrics, weights);
    }

    public List<CompanyAnalyticsDto> getAgentComparisonAnalytics(String companyName, CostWeightsDto weights) {
        if (weights == null) weights = new CostWeightsDto();

        List<CloudMetricEntity> allMetrics =
                cloudMetricRepository.findAllByCompanyInfoEntity_CompanyName(companyName);

        Map<String, List<CloudMetricEntity>> agentMetricsMap = allMetrics.stream()
                .filter(m -> m.getAgentInfoEntity() != null && m.getAgentInfoEntity().getAgentId() != null)
                .collect(Collectors.groupingBy(m -> m.getAgentInfoEntity().getAgentId()));

        List<CompanyAnalyticsDto> agentAnalytics = new ArrayList<>();
        for (Map.Entry<String, List<CloudMetricEntity>> entry : agentMetricsMap.entrySet()) {
            agentAnalytics.add(computeAnalytics(entry.getKey(), entry.getValue(), weights));
        }
        return agentAnalytics;
    }

    private CompanyAnalyticsDto computeAnalytics(String entityName, List<CloudMetricEntity> allMetrics, CostWeightsDto weights) {
        CompanyAnalyticsDto dto = new CompanyAnalyticsDto();
        dto.setCompanyName(entityName);
        dto.setTotalSamples(allMetrics.size());
        dto.setAppliedWeights(weights);

        // Only process records that have an attached audit metric
        List<CloudMetricEntity> metrics = allMetrics.stream()
                .filter(m -> m.getCloudAuditMetricEntity() != null)
                .collect(Collectors.toList());

        if (metrics.isEmpty()) return dto;

        ZonedDateTime now      = ZonedDateTime.now(UTC);
        String curMonthKey     = now.format(MONTH_FMT);
        String prevMonthKey    = now.minusMonths(1).format(MONTH_FMT);
        String todayKey        = now.format(DAY_FMT);

        // ── Headline accumulators ───────────────────────────────────────────
        double totalAllTimeCost = 0;
        double curMonthCost     = 0;
        double prevMonthCost    = 0;
        double todayCost        = 0;

        double sumCpu = 0, peakCpu = 0;
        double sumMem = 0, peakMem = 0;
        double sumDisk = 0;
        double sumNetIn = 0, sumNetOut = 0;
        double sumResponseTime = 0, peakResponseTime = 0;
        double sumErrorTrend = 0;
        double sumAvailability = 0;
        double sumAppHealth = 0;
        double sumQPS = 0, peakQPS = 0;
        double sumCacheEff = 0;
        double sumQueryEff = 0;
        double sumConnUtil = 0;
        double sumResourceUtil = 0;
        double sumCostGrowth = 0;
        double sumCpuAnomaly = 0, sumMemAnomaly = 0, sumNetAnomaly = 0,
               sumDbAnomaly = 0, sumRespAnomaly = 0, sumOverallAnomaly = 0;

        // Cost breakdown percentages (average across records)
        double sumComputePct = 0, sumMemPct = 0, sumNetPct = 0, sumStorePct = 0;

        // Time-series: day → [cost, cpuAvg, memAvg, netIn, netOut, errorTrend, count]
        Map<String, double[]> dayMap   = new LinkedHashMap<>();
        // month → [cost, cpuAvg, memAvg, count]
        Map<String, double[]> monthMap = new LinkedHashMap<>();

        for (CloudMetricEntity m : metrics) {
            CloudAuditMetricEntity a = m.getCloudAuditMetricEntity();

            ZonedDateTime ts = m.getLocalDateTime() != null
                    ? m.getLocalDateTime().atZone(UTC)
                    : now;
            String dayKey   = ts.format(DAY_FMT);
            String monthKey = ts.format(MONTH_FMT);

            double computeCost = a.getAverageCpuUsage() * weights.getCpuCostPerPercent();
            double memCost     = a.getAverageMemoryUsage() * weights.getMemCostPerGB();
            double diskCost    = a.getAverageDiskUsage() * weights.getDiskCostPerGB();
            double netCost     = a.getAverageNetworkUsage() * weights.getNetworkCostPerMB();

            double cost  = computeCost + memCost + diskCost + netCost;
            double daily = cost / 30.0; // Assuming cost is a monthly snapshot

            // Headline costs
            totalAllTimeCost += cost;
            if (monthKey.equals(curMonthKey))  curMonthCost  += cost;
            if (monthKey.equals(prevMonthKey)) prevMonthCost += cost;
            if (dayKey.equals(todayKey))       todayCost     += daily;

            // Utilisation
            sumCpu          += a.getAverageCpuUsage();
            if (a.getPeakCpuUsage() > peakCpu) peakCpu = a.getPeakCpuUsage();
            sumMem          += a.getAverageMemoryUsage();
            if (a.getPeakMemoryUsage() > peakMem) peakMem = a.getPeakMemoryUsage();
            sumDisk         += a.getAverageDiskUsage();
            sumNetIn        += a.getAverageNetworkUsage();

            // Application
            sumResponseTime += a.getAverageResponseTime();
            if (a.getPeakResponseTime() > peakResponseTime) peakResponseTime = a.getPeakResponseTime();
            sumErrorTrend   += a.getErrorTrend();
            sumAvailability += a.getAvailabilityScore();
            sumAppHealth    += a.getApplicationHealthScore();

            // Database
            sumQPS          += a.getAverageQPS();
            if (a.getPeakQPS() > peakQPS) peakQPS = a.getPeakQPS();
            sumCacheEff     += a.getCacheEfficiencyScore();
            sumQueryEff     += a.getQueryEfficiencyScore();
            sumConnUtil     += a.getConnectionUtilization();

            // Cost breakdown percentages
            sumComputePct   += a.getComputeCostPercentage();
            sumMemPct       += a.getMemoryCostPercentage();
            sumNetPct       += a.getNetworkCostPercentage();
            sumStorePct     += a.getStorageCostPercentage();

            // Growth / anomaly
            sumResourceUtil   += a.getResourceUtilizationScore();
            sumCostGrowth     += a.getCostGrowthRate();
            sumCpuAnomaly     += a.getCpuAnomalyScore();
            sumMemAnomaly     += a.getMemoryAnomalyScore();
            sumNetAnomaly     += a.getNetworkAnomalyScore();
            sumDbAnomaly      += a.getDatabaseAnomalyScore();
            sumRespAnomaly    += a.getResponseTimeAnomalyScore();
            sumOverallAnomaly += a.getOverallAnomalyScore();

            // Daily time-series — [0]=cost [1]=cpu [2]=mem [3]=netIn [4]=errTrend [5]=count
            double[] d = dayMap.computeIfAbsent(dayKey, k -> new double[6]);
            d[0] += cost; d[1] += a.getAverageCpuUsage(); d[2] += a.getAverageMemoryUsage();
            d[3] += a.getAverageNetworkUsage(); d[4] += a.getErrorTrend(); d[5]++;

            // Monthly time-series — [0]=cost [1]=cpu [2]=mem [3]=count
            double[] mo = monthMap.computeIfAbsent(monthKey, k -> new double[4]);
            mo[0] += cost; mo[1] += a.getAverageCpuUsage(); mo[2] += a.getAverageMemoryUsage(); mo[3]++;
        }

        int n = metrics.size();

        // ── Set headline scalars ──────────────────────────────────────────
        dto.setTotalAllTimeCost(totalAllTimeCost);
        dto.setCurrentMonthCost(curMonthCost);
        dto.setPreviousMonthCost(prevMonthCost);
        dto.setTodayCost(todayCost);

        double mom = prevMonthCost > 0
                ? ((curMonthCost - prevMonthCost) / prevMonthCost) * 100.0 : 0;
        dto.setMonthOverMonthChangePercent(mom);

        dto.setAvgCpuPercent(sumCpu / n);
        dto.setPeakCpuPercent(peakCpu);
        dto.setAvgMemPercent(sumMem / n);
        dto.setPeakMemPercent(peakMem);
        dto.setAvgDiskUsagePercent(sumDisk / n);
        dto.setAvgNetworkInMBps(sumNetIn / n);

        dto.setAvgResponseTimeMs(sumResponseTime / n);
        dto.setAvgErrorRatePercent(sumErrorTrend / n);
        dto.setAvgCacheHitRatio(sumCacheEff / n / 100.0); // stored as percent, expose as ratio
        dto.setAvgDbQueriesPerSec(sumQPS / n);

        // Cost breakdown — average of the per-record percentages
        Map<String, Double> breakdown = new LinkedHashMap<>();
        breakdown.put("Compute", round1(sumComputePct / n));
        breakdown.put("Memory",  round1(sumMemPct    / n));
        breakdown.put("Network", round1(sumNetPct    / n));
        breakdown.put("Storage", round1(sumStorePct  / n));
        dto.setCostBreakdownPercent(breakdown);

        // ── Daily snapshots (last 30 days) ─────────────────────────────────
        ZonedDateTime cutoff = now.minusDays(30);
        List<DailySnapshot> dailyList = dayMap.entrySet().stream()
                .filter(e -> {
                    ZonedDateTime d = ZonedDateTime.parse(e.getKey() + "T00:00:00Z");
                    return !d.isBefore(cutoff);
                })
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    double[] v = e.getValue();
                    int cnt = (int) v[5];
                    return new DailySnapshot(
                            e.getKey(),
                            v[0],                // total cost for day
                            cnt > 0 ? v[1]/cnt : 0,  // avg cpu
                            cnt > 0 ? v[2]/cnt : 0,  // avg mem
                            0,                   // disk — not in audit entity (use 0)
                            cnt > 0 ? v[3]/cnt : 0,  // avg netIn
                            0,                   // netOut — not separately tracked
                            cnt > 0 ? v[4]/cnt : 0,  // avg errorTrend
                            cnt
                    );
                })
                .collect(Collectors.toList());
        dto.setDailySnapshots(dailyList);

        // ── Monthly snapshots (last 12 months) ────────────────────────────
        ZonedDateTime monthCutoff = now.minusMonths(12);
        List<MonthlySnapshot> monthlyList = monthMap.entrySet().stream()
                .filter(e -> {
                    ZonedDateTime m = ZonedDateTime.parse(e.getKey() + "-01T00:00:00Z");
                    return !m.isBefore(monthCutoff);
                })
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    double[] v = e.getValue();
                    int cnt = (int) v[3];
                    return new MonthlySnapshot(
                            e.getKey(),
                            v[0],
                            cnt > 0 ? v[1]/cnt : 0,
                            cnt > 0 ? v[2]/cnt : 0,
                            cnt
                    );
                })
                .collect(Collectors.toList());
        dto.setMonthlySnapshots(monthlyList);

        return dto;
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
