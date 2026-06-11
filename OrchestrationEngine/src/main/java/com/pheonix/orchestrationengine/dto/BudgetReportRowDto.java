package com.pheonix.orchestrationengine.dto;

/**
 * Represents a single row from the uploaded CSV budget report,
 * enriched with the corresponding audit-metric actuals and deltas.
 *
 * CSV columns expected (case-insensitive, flexible separators):
 *   Service Category / Cost Center | Budget Allocated | Actual Spend |
 *   Prior Month Spend | MoM Variance ($) | MoM Variance (%)
 */
public class BudgetReportRowDto {

    // ── From CSV ──────────────────────────────────────────────────────────
    private String serviceCategory;   // e.g. "Compute", "Memory / RAM", "Network"
    private double budgetAllocated;
    private double actualSpend;       // CSV-reported actual spend
    private double priorMonthSpend;
    private double momVarianceDollar; // CSV-reported MoM $ variance
    private double momVariancePercent;// CSV-reported MoM % variance

    // ── Matched audit category ────────────────────────────────────────────
    private String matchedAuditCategory; // "Compute" | "Memory" | "Network" | "Storage" | "Overall"

    // ── Audit-system actuals (derived from live CloudAuditMetric data) ────
    private double auditActualSpend;  // what the audit system computed for this category
    private double auditPriorMonthSpend;
    private double auditMomVarianceDollar;
    private double auditMomVariancePercent;

    // ── Variances (CSV vs Budget) ─────────────────────────────────────────
    private double budgetVarianceDollar;   // actualSpend − budgetAllocated
    private double budgetVariancePercent;  // (actualSpend − budget) / budget * 100

    // ── Variances (Audit vs CSV Actual) ──────────────────────────────────
    private double auditVsCsvDollar;       // auditActual − csvActual
    private double auditVsCsvPercent;      // (auditActual − csvActual) / csvActual * 100

    // ── Audit utilisation snapshot (for the matching resource) ────────────
    private double auditAvgUtilizationPercent; // e.g. avgCpu, avgMem, avgDisk, avgNetwork
    private double auditCostSharePercent;       // e.g. computeCostPercentage from audit

    public BudgetReportRowDto() {}

    // ── Getters / Setters ─────────────────────────────────────────────────
    public String getServiceCategory() { return serviceCategory; }
    public void setServiceCategory(String v) { this.serviceCategory = v; }

    public double getBudgetAllocated() { return budgetAllocated; }
    public void setBudgetAllocated(double v) { this.budgetAllocated = v; }

    public double getActualSpend() { return actualSpend; }
    public void setActualSpend(double v) { this.actualSpend = v; }

    public double getPriorMonthSpend() { return priorMonthSpend; }
    public void setPriorMonthSpend(double v) { this.priorMonthSpend = v; }

    public double getMomVarianceDollar() { return momVarianceDollar; }
    public void setMomVarianceDollar(double v) { this.momVarianceDollar = v; }

    public double getMomVariancePercent() { return momVariancePercent; }
    public void setMomVariancePercent(double v) { this.momVariancePercent = v; }

    public String getMatchedAuditCategory() { return matchedAuditCategory; }
    public void setMatchedAuditCategory(String v) { this.matchedAuditCategory = v; }

    public double getAuditActualSpend() { return auditActualSpend; }
    public void setAuditActualSpend(double v) { this.auditActualSpend = v; }

    public double getAuditPriorMonthSpend() { return auditPriorMonthSpend; }
    public void setAuditPriorMonthSpend(double v) { this.auditPriorMonthSpend = v; }

    public double getAuditMomVarianceDollar() { return auditMomVarianceDollar; }
    public void setAuditMomVarianceDollar(double v) { this.auditMomVarianceDollar = v; }

    public double getAuditMomVariancePercent() { return auditMomVariancePercent; }
    public void setAuditMomVariancePercent(double v) { this.auditMomVariancePercent = v; }

    public double getBudgetVarianceDollar() { return budgetVarianceDollar; }
    public void setBudgetVarianceDollar(double v) { this.budgetVarianceDollar = v; }

    public double getBudgetVariancePercent() { return budgetVariancePercent; }
    public void setBudgetVariancePercent(double v) { this.budgetVariancePercent = v; }

    public double getAuditVsCsvDollar() { return auditVsCsvDollar; }
    public void setAuditVsCsvDollar(double v) { this.auditVsCsvDollar = v; }

    public double getAuditVsCsvPercent() { return auditVsCsvPercent; }
    public void setAuditVsCsvPercent(double v) { this.auditVsCsvPercent = v; }

    public double getAuditAvgUtilizationPercent() { return auditAvgUtilizationPercent; }
    public void setAuditAvgUtilizationPercent(double v) { this.auditAvgUtilizationPercent = v; }

    public double getAuditCostSharePercent() { return auditCostSharePercent; }
    public void setAuditCostSharePercent(double v) { this.auditCostSharePercent = v; }
}
