package com.pheonix.orchestrationengine.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ApplicationInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int requestsPerMinute;
    private double errorRatePercent;
    private double responseTimeMs;

    public ApplicationInfoEntity() {
    }

    public ApplicationInfoEntity(int requestsPerMinute, double errorRatePercent, double responseTimeMs) {
        this.requestsPerMinute = requestsPerMinute;
        this.errorRatePercent = errorRatePercent;
        this.responseTimeMs = responseTimeMs;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public double getErrorRatePercent() {
        return errorRatePercent;
    }

    public void setErrorRatePercent(double errorRatePercent) {
        this.errorRatePercent = errorRatePercent;
    }

    public double getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(double responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }
}
