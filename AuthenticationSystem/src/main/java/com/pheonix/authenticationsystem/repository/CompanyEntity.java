package com.pheonix.authenticationsystem.repository;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String companyName;

    private String subscription;

    private LocalDateTime createdAt;

    public CompanyEntity() {
    }

    public CompanyEntity(String companyName) {
        this.companyName = companyName;
        this.subscription = "active";
        this.createdAt = LocalDateTime.now();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
