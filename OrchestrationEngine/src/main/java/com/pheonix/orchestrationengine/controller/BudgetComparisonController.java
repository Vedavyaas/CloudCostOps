package com.pheonix.orchestrationengine.controller;

import com.pheonix.orchestrationengine.dto.BudgetComparisonResultDto;
import com.pheonix.orchestrationengine.service.BudgetComparisonService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Accepts a CSV budget report (multipart/form-data) and a report-month string,
 * runs the full comparison against live audit data, and returns the result JSON.
 *
 * Route (through API Gateway): POST /ORCHESTRATIONENGINE/get/analytics/budget-comparison
 */
@RestController
public class BudgetComparisonController {

    private final BudgetComparisonService budgetComparisonService;

    public BudgetComparisonController(BudgetComparisonService budgetComparisonService) {
        this.budgetComparisonService = budgetComparisonService;
    }

    /**
     * Multipart upload endpoint.
     *
     * @param jwt         injected JWT — used to extract company-name claim
     * @param reportMonth e.g. "2026-06"  (sent as a form field by the frontend)
     * @param file        the .csv budget report
     */
    @PostMapping(
        value    = "/get/analytics/budget-comparison",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ANALYST')")
    public ResponseEntity<BudgetComparisonResultDto> compareBudget(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("month") String reportMonth,
            @RequestPart("file")   MultipartFile file
    ) throws IOException {

        String companyName = jwt.getClaim("company-name");
        String csvText     = new String(file.getBytes(), StandardCharsets.UTF_8);

        BudgetComparisonResultDto result =
                budgetComparisonService.compare(companyName, reportMonth, csvText);

        return ResponseEntity.ok(result);
    }
}
