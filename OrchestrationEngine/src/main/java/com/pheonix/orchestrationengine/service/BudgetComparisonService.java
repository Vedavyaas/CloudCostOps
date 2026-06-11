package com.pheonix.orchestrationengine.service;

import com.pheonix.orchestrationengine.dto.BudgetComparisonResultDto;
import com.pheonix.orchestrationengine.dto.BudgetReportRowDto;
import com.pheonix.orchestrationengine.dto.CompanyAnalyticsDto;
import com.pheonix.orchestrationengine.dto.CostWeightsDto;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Parses an uploaded CSV budget report and compares each row against the
 * live audit-metric data already stored in the system.
 *
 * All computation stays here — the controller and frontend are dumb consumers.
 */
@Service
public class BudgetComparisonService {

    /* Maps lower-cased keywords found in the CSV "Service Category" column
       to the canonical audit category name used in CompanyAnalyticsDto. */
    private static final Map<String, String> KEYWORD_TO_AUDIT = new LinkedHashMap<>();
    static {
        KEYWORD_TO_AUDIT.put("compute",  "Compute");
        KEYWORD_TO_AUDIT.put("cpu",      "Compute");
        KEYWORD_TO_AUDIT.put("server",   "Compute");
        KEYWORD_TO_AUDIT.put("vm",       "Compute");
        KEYWORD_TO_AUDIT.put("memory",   "Memory");
        KEYWORD_TO_AUDIT.put("mem",      "Memory");
        KEYWORD_TO_AUDIT.put("ram",      "Memory");
        KEYWORD_TO_AUDIT.put("network",  "Network");
        KEYWORD_TO_AUDIT.put("net",      "Network");
        KEYWORD_TO_AUDIT.put("egress",   "Network");
        KEYWORD_TO_AUDIT.put("bandwidth","Network");
        KEYWORD_TO_AUDIT.put("storage",  "Storage");
        KEYWORD_TO_AUDIT.put("disk",     "Storage");
        KEYWORD_TO_AUDIT.put("s3",       "Storage");
        KEYWORD_TO_AUDIT.put("blob",     "Storage");
        KEYWORD_TO_AUDIT.put("database", "Storage");
        KEYWORD_TO_AUDIT.put("db",       "Storage");
    }

    private final AnalysisService analysisService;

    public BudgetComparisonService(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /**
     * Main entry point.
     *
     * @param companyName  extracted from JWT
     * @param reportMonth  e.g. "2026-06" (passed by the frontend from the month picker)
     * @param csvText      raw CSV string uploaded by the user
     * @return             fully-computed comparison result
     */
    public BudgetComparisonResultDto compare(String companyName, String reportMonth, String csvText) {

        // 1. Parse the CSV into raw rows
        List<CsvRow> csvRows = parseCsv(csvText);

        // 2. Fetch audit analytics SCOPED TO THE CHOSEN MONTH from the DB.
        //    This ensures cost breakdowns, utilisation averages, and MoM values
        //    are all derived from records recorded during that specific month —
        //    not a global all-time average.
        CompanyAnalyticsDto audit = analysisService.getCompanyAnalyticsForMonth(
                companyName, reportMonth, new CostWeightsDto());

        // 3. Build result
        BudgetComparisonResultDto result = new BudgetComparisonResultDto();
        result.setCompanyName(companyName);
        result.setReportMonth(reportMonth);
        result.setRowCount(csvRows.size());

        // Populate audit headline metrics (month-scoped)
        result.setAuditAvgCpuPercent(audit.getAvgCpuPercent());
        result.setAuditAvgMemPercent(audit.getAvgMemPercent());
        result.setAuditAvgDiskPercent(audit.getAvgDiskUsagePercent());
        result.setAuditAvgNetworkMBps(audit.getAvgNetworkInMBps());
        result.setAuditAvgErrorRatePercent(audit.getAvgErrorRatePercent());
        result.setAuditMonthOverMonthChangePercent(audit.getMonthOverMonthChangePercent());

        // These are now month-specific totals from the DB
        double auditCurrentTotal  = audit.getCurrentMonthCost();
        double auditPreviousTotal = audit.getPreviousMonthCost();

        // Flag if there are no DB records for this month so frontend can warn the user
        int auditSamples = audit.getTotalSamples();
        result.setAuditSampleCount(auditSamples);
        result.setNoDataForMonth(auditSamples == 0);

        // 4. Build per-row DTOs
        List<BudgetReportRowDto> rows = new ArrayList<>();

        for (CsvRow csv : csvRows) {
            BudgetReportRowDto row = new BudgetReportRowDto();
            row.setServiceCategory(csv.service);
            row.setBudgetAllocated(csv.budget);
            row.setActualSpend(csv.actual);
            row.setPriorMonthSpend(csv.prior);
            row.setMomVarianceDollar(csv.momDollar);
            row.setMomVariancePercent(csv.momPercent);

            // Match CSV category → audit category
            String auditCat = matchCategory(csv.service);
            row.setMatchedAuditCategory(auditCat != null ? auditCat : "Overall");

            // Derive audit spend for this category using the cost breakdown percentages
            double auditCatSharePct  = 0.0;
            double auditUtilPct      = 0.0;

            Map<String, Double> breakdown = audit.getCostBreakdownPercent();
            if (auditCat != null && breakdown != null && breakdown.containsKey(auditCat)) {
                auditCatSharePct = breakdown.get(auditCat);
            } else if (breakdown != null) {
                // fallback: proportional to CSV actual within CSV total
                double csvTotal = csvRows.stream().mapToDouble(r -> r.actual).sum();
                auditCatSharePct = csvTotal > 0
                        ? (csv.actual / csvTotal) * 100.0
                        : (breakdown.values().stream().mapToDouble(Double::doubleValue).sum() / breakdown.size());
            }

            // Audit actual / prior for this category ($ value, not %)
            double auditActual = auditCurrentTotal  * (auditCatSharePct / 100.0);
            double auditPrior  = auditPreviousTotal * (auditCatSharePct / 100.0);

            // Utilisation % for the matched resource category
            if      ("Compute".equals(auditCat)) auditUtilPct = audit.getAvgCpuPercent();
            else if ("Memory" .equals(auditCat)) auditUtilPct = audit.getAvgMemPercent();
            else if ("Storage".equals(auditCat)) auditUtilPct = audit.getAvgDiskUsagePercent();
            else if ("Network".equals(auditCat)) auditUtilPct = audit.getAvgNetworkInMBps(); // MB/s, not %

            row.setAuditCostSharePercent(round2(auditCatSharePct));
            row.setAuditAvgUtilizationPercent(round2(auditUtilPct));
            row.setAuditActualSpend(round2(auditActual));
            row.setAuditPriorMonthSpend(round2(auditPrior));

            double auditMomDol = auditActual - auditPrior;
            double auditMomPct = auditPrior > 0 ? (auditMomDol / auditPrior) * 100.0 : 0.0;
            row.setAuditMomVarianceDollar(round2(auditMomDol));
            row.setAuditMomVariancePercent(round2(auditMomPct));

            // Budget variance (CSV actual vs budget)
            double budgetVarDol = csv.actual - csv.budget;
            double budgetVarPct = csv.budget != 0 ? (budgetVarDol / csv.budget) * 100.0 : 0.0;
            row.setBudgetVarianceDollar(round2(budgetVarDol));
            row.setBudgetVariancePercent(round2(budgetVarPct));

            // Audit vs CSV variance
            double auditVsCsvDol = auditActual - csv.actual;
            double auditVsCsvPct = csv.actual != 0 ? (auditVsCsvDol / csv.actual) * 100.0 : 0.0;
            row.setAuditVsCsvDollar(round2(auditVsCsvDol));
            row.setAuditVsCsvPercent(round2(auditVsCsvPct));

            rows.add(row);
        }

        result.setRows(rows);

        // 5. Aggregate totals
        double totalBudget         = rows.stream().mapToDouble(BudgetReportRowDto::getBudgetAllocated).sum();
        double totalCsvActual      = rows.stream().mapToDouble(BudgetReportRowDto::getActualSpend).sum();
        double totalCsvPrior       = rows.stream().mapToDouble(BudgetReportRowDto::getPriorMonthSpend).sum();
        double totalCsvMomDol      = rows.stream().mapToDouble(BudgetReportRowDto::getMomVarianceDollar).sum();
        double totalAuditActual    = rows.stream().mapToDouble(BudgetReportRowDto::getAuditActualSpend).sum();
        double totalAuditPrior     = rows.stream().mapToDouble(BudgetReportRowDto::getAuditPriorMonthSpend).sum();
        double totalAuditMomDol    = totalAuditActual - totalAuditPrior;
        double totalBudgetVarDol   = totalCsvActual  - totalBudget;
        double totalAuditVsCsvDol  = totalAuditActual - totalCsvActual;

        result.setTotalBudgetAllocated(round2(totalBudget));
        result.setTotalCsvActualSpend(round2(totalCsvActual));
        result.setTotalCsvPriorMonthSpend(round2(totalCsvPrior));
        result.setTotalCsvMomVarianceDollar(round2(totalCsvMomDol));
        result.setTotalCsvMomVariancePercent(round2(totalCsvPrior > 0 ? (totalCsvMomDol / totalCsvPrior) * 100.0 : 0));
        result.setTotalAuditActualSpend(round2(totalAuditActual));
        result.setTotalAuditPriorMonthSpend(round2(totalAuditPrior));
        result.setTotalAuditMomVarianceDollar(round2(totalAuditMomDol));
        result.setTotalAuditMomVariancePercent(round2(totalAuditPrior > 0 ? (totalAuditMomDol / totalAuditPrior) * 100.0 : 0));
        result.setTotalBudgetVarianceDollar(round2(totalBudgetVarDol));
        result.setTotalBudgetVariancePercent(round2(totalBudget > 0 ? (totalBudgetVarDol / totalBudget) * 100.0 : 0));
        result.setTotalAuditVsCsvDollar(round2(totalAuditVsCsvDol));
        result.setTotalAuditVsCsvPercent(round2(totalCsvActual > 0 ? (totalAuditVsCsvDol / totalCsvActual) * 100.0 : 0));

        return result;
    }

    // ── CSV parsing ───────────────────────────────────────────────────────

    private static class CsvRow {
        String service;
        double budget, actual, prior, momDollar, momPercent;
    }

    /**
     * Flexible CSV parser: detects columns by header keywords (case-insensitive).
     * Strips leading $ signs and whitespace. Skips blank or all-zero rows.
     */
    private List<CsvRow> parseCsv(String csvText) {
        List<CsvRow> rows = new ArrayList<>();
        if (csvText == null || csvText.isBlank()) return rows;

        String[] lines = csvText.trim().split("\\r?\\n");
        if (lines.length < 2) return rows;

        // Detect column indices from header
        String[] header = splitLine(lines[0]);
        int idxService = -1, idxBudget = -1, idxActual = -1,
                idxPrior = -1, idxMomDol = -1, idxMomPct = -1;

        for (int i = 0; i < header.length; i++) {
            String h = header[i].toLowerCase().replaceAll("[^a-z%$/ ]", "").trim();
            if (idxService < 0 && (h.contains("service") || h.contains("category") || h.contains("cost center"))) idxService = i;
            else if (idxBudget < 0 && h.contains("budget"))                                                        idxBudget  = i;
            else if (idxActual < 0 && h.contains("actual"))                                                        idxActual  = i;
            else if (idxPrior  < 0 && (h.contains("prior") || h.contains("previous")))                             idxPrior   = i;
            else if (idxMomDol < 0 && h.contains("variance") && (h.contains("$") || h.contains("dollar")))         idxMomDol  = i;
            else if (idxMomPct < 0 && h.contains("variance") && h.contains("%"))                                   idxMomPct  = i;
        }

        // Fallback column positions if header detection missed any
        if (idxService < 0) idxService = 0;
        if (idxBudget  < 0) idxBudget  = Math.min(1, header.length - 1);
        if (idxActual  < 0) idxActual  = Math.min(2, header.length - 1);
        if (idxPrior   < 0) idxPrior   = Math.min(3, header.length - 1);
        if (idxMomDol  < 0) idxMomDol  = Math.min(4, header.length - 1);
        if (idxMomPct  < 0) idxMomPct  = Math.min(5, header.length - 1);

        for (int i = 1; i < lines.length; i++) {
            String[] cols = splitLine(lines[i]);
            if (cols.length == 0 || cols[0].isBlank()) continue;

            CsvRow row = new CsvRow();
            row.service   = safe(cols, idxService);
            row.budget    = parseNum(cols, idxBudget);
            row.actual    = parseNum(cols, idxActual);
            row.prior     = parseNum(cols, idxPrior);
            row.momDollar = parseNum(cols, idxMomDol);
            row.momPercent= parseNum(cols, idxMomPct);

            // Derive MoM if missing
            if (row.momDollar == 0 && row.actual != 0 && row.prior != 0)
                row.momDollar = row.actual - row.prior;
            if (row.momPercent == 0 && row.prior != 0)
                row.momPercent = (row.momDollar / row.prior) * 100.0;

            rows.add(row);
        }
        return rows;
    }

    private String[] splitLine(String line) {
        // Handle quoted CSVs simply by stripping quotes, then splitting on comma
        return line.replaceAll("\"", "").split(",", -1);
    }

    private String safe(String[] cols, int idx) {
        return (idx >= 0 && idx < cols.length) ? cols[idx].trim() : "";
    }

    private double parseNum(String[] cols, int idx) {
        if (idx < 0 || idx >= cols.length) return 0.0;
        try {
            return Double.parseDouble(cols[idx].trim().replaceAll("[$,%]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // ── Category matching ─────────────────────────────────────────────────

    private String matchCategory(String serviceName) {
        if (serviceName == null) return null;
        String lower = serviceName.toLowerCase();
        for (Map.Entry<String, String> entry : KEYWORD_TO_AUDIT.entrySet()) {
            if (lower.contains(entry.getKey())) return entry.getValue();
        }
        return null;
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
