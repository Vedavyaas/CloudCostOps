package com.pheonix.orchestrationengine.controller;

import com.pheonix.orchestrationengine.dto.CompanyAnalyticsDto;
import com.pheonix.orchestrationengine.dto.CostWeightsDto;
import com.pheonix.orchestrationengine.service.AnalysisService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {

    private final AnalysisService analysisService;

    public AnalyticsController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/get/analytics/summary")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ANALYST')")
    public ResponseEntity<CompanyAnalyticsDto> getCompanyAnalyticsSummary(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "cpuCost",  required = false) Double cpuCost,
            @RequestParam(name = "memCost",  required = false) Double memCost,
            @RequestParam(name = "diskCost", required = false) Double diskCost,
            @RequestParam(name = "netCost",  required = false) Double netCost) {

        CostWeightsDto weights = new CostWeightsDto();
        if (cpuCost  != null) weights.setCpuCostPerPercent(cpuCost);
        if (memCost  != null) weights.setMemCostPerGB(memCost);
        if (diskCost != null) weights.setDiskCostPerGB(diskCost);
        if (netCost  != null) weights.setNetworkCostPerMB(netCost);

        String company = jwt.getClaim("company-name");
        return ResponseEntity.ok(analysisService.getCompanyAnalytics(company, weights));
    }

    @GetMapping("/get/analytics/agents/compare")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ANALYST')")
    public ResponseEntity<List<CompanyAnalyticsDto>> getAgentComparisonAnalytics(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "cpuCost",  required = false) Double cpuCost,
            @RequestParam(name = "memCost",  required = false) Double memCost,
            @RequestParam(name = "diskCost", required = false) Double diskCost,
            @RequestParam(name = "netCost",  required = false) Double netCost) {

        CostWeightsDto weights = new CostWeightsDto();
        if (cpuCost  != null) weights.setCpuCostPerPercent(cpuCost);
        if (memCost  != null) weights.setMemCostPerGB(memCost);
        if (diskCost != null) weights.setDiskCostPerGB(diskCost);
        if (netCost  != null) weights.setNetworkCostPerMB(netCost);

        String company = jwt.getClaim("company-name");
        return ResponseEntity.ok(analysisService.getAgentComparisonAnalytics(company, weights));
    }
}