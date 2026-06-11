package com.pheonix.orchestrationengine.dto;

import java.util.List;

/**
 * Full response returned by POST /get/analytics/budget-comparison.
 * Contains both the per-row detail and aggregate summary KPIs.
 */
public class BudgetComparisonResultDto {

    // ── Metadata ──────────────────────────────────────────────────────────
    private String  companyName;
    private String  reportMonth;          // e.g. "2026-06"
    private int     rowCount;
    private int     auditSampleCount;     // how many DB records matched the chosen month
    private boolean noDataForMonth;       // true if auditSampleCount == 0

    // ── Summary totals ────────────────────────────────────────────────────
    private double totalBudgetAllocated;

    // CSV-reported totals
    private double totalCsvActualSpend;
    private double totalCsvPriorMonthSpend;
    private double totalCsvMomVarianceDollar;
    private double totalCsvMomVariancePercent;

    // Audit-system totals
    private double totalAuditActualSpend;
    private double totalAuditPriorMonthSpend;
    private double totalAuditMomVarianceDollar;
    private double totalAuditMomVariancePercent;

    // Derived aggregate variances
    private double totalBudgetVarianceDollar;   // csvActual − budget
    private double totalBudgetVariancePercent;
    private double totalAuditVsCsvDollar;        // auditActual − csvActual
    private double totalAuditVsCsvPercent;

    // Audit headline metrics (for context cards)
    private double auditAvgCpuPercent;
    private double auditAvgMemPercent;
    private double auditAvgDiskPercent;
    private double auditAvgNetworkMBps;
    private double auditAvgErrorRatePercent;
    private double auditMonthOverMonthChangePercent;

    // ── Per-row detail ────────────────────────────────────────────────────
    private List<BudgetReportRowDto> rows;

    public BudgetComparisonResultDto() {}

    // ── Getters / Setters ─────────────────────────────────────────────────
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String v) { this.companyName = v; }

    public String getReportMonth() { return reportMonth; }
    public void setReportMonth(String v) { this.reportMonth = v; }

    public int getRowCount() { return rowCount; }
    public void setRowCount(int v) { this.rowCount = v; }

    public int isAuditSampleCount() { return auditSampleCount; }
    public void setAuditSampleCount(int v) { this.auditSampleCount = v; }

    public boolean isNoDataForMonth() { return noDataForMonth; }
    public void setNoDataForMonth(boolean v) { this.noDataForMonth = v; }

    public double getTotalBudgetAllocated() { return totalBudgetAllocated; }
    public void setTotalBudgetAllocated(double v) { this.totalBudgetAllocated = v; }

    public double getTotalCsvActualSpend() { return totalCsvActualSpend; }
    public void setTotalCsvActualSpend(double v) { this.totalCsvActualSpend = v; }

    public double getTotalCsvPriorMonthSpend() { return totalCsvPriorMonthSpend; }
    public void setTotalCsvPriorMonthSpend(double v) { this.totalCsvPriorMonthSpend = v; }

    public double getTotalCsvMomVarianceDollar() { return totalCsvMomVarianceDollar; }
    public void setTotalCsvMomVarianceDollar(double v) { this.totalCsvMomVarianceDollar = v; }

    public double getTotalCsvMomVariancePercent() { return totalCsvMomVariancePercent; }
    public void setTotalCsvMomVariancePercent(double v) { this.totalCsvMomVariancePercent = v; }

    public double getTotalAuditActualSpend() { return totalAuditActualSpend; }
    public void setTotalAuditActualSpend(double v) { this.totalAuditActualSpend = v; }

    public double getTotalAuditPriorMonthSpend() { return totalAuditPriorMonthSpend; }
    public void setTotalAuditPriorMonthSpend(double v) { this.totalAuditPriorMonthSpend = v; }

    public double getTotalAuditMomVarianceDollar() { return totalAuditMomVarianceDollar; }
    public void setTotalAuditMomVarianceDollar(double v) { this.totalAuditMomVarianceDollar = v; }

    public double getTotalAuditMomVariancePercent() { return totalAuditMomVariancePercent; }
    public void setTotalAuditMomVariancePercent(double v) { this.totalAuditMomVariancePercent = v; }

    public double getTotalBudgetVarianceDollar() { return totalBudgetVarianceDollar; }
    public void setTotalBudgetVarianceDollar(double v) { this.totalBudgetVarianceDollar = v; }

    public double getTotalBudgetVariancePercent() { return totalBudgetVariancePercent; }
    public void setTotalBudgetVariancePercent(double v) { this.totalBudgetVariancePercent = v; }

    public double getTotalAuditVsCsvDollar() { return totalAuditVsCsvDollar; }
    public void setTotalAuditVsCsvDollar(double v) { this.totalAuditVsCsvDollar = v; }

    public double getTotalAuditVsCsvPercent() { return totalAuditVsCsvPercent; }
    public void setTotalAuditVsCsvPercent(double v) { this.totalAuditVsCsvPercent = v; }

    public double getAuditAvgCpuPercent() { return auditAvgCpuPercent; }
    public void setAuditAvgCpuPercent(double v) { this.auditAvgCpuPercent = v; }

    public double getAuditAvgMemPercent() { return auditAvgMemPercent; }
    public void setAuditAvgMemPercent(double v) { this.auditAvgMemPercent = v; }

    public double getAuditAvgDiskPercent() { return auditAvgDiskPercent; }
    public void setAuditAvgDiskPercent(double v) { this.auditAvgDiskPercent = v; }

    public double getAuditAvgNetworkMBps() { return auditAvgNetworkMBps; }
    public void setAuditAvgNetworkMBps(double v) { this.auditAvgNetworkMBps = v; }

    public double getAuditAvgErrorRatePercent() { return auditAvgErrorRatePercent; }
    public void setAuditAvgErrorRatePercent(double v) { this.auditAvgErrorRatePercent = v; }

    public double getAuditMonthOverMonthChangePercent() { return auditMonthOverMonthChangePercent; }
    public void setAuditMonthOverMonthChangePercent(double v) { this.auditMonthOverMonthChangePercent = v; }

    public List<BudgetReportRowDto> getRows() { return rows; }
    public void setRows(List<BudgetReportRowDto> v) { this.rows = v; }
}
