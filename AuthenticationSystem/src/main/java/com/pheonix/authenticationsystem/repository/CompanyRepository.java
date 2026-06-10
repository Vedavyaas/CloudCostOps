package com.pheonix.authenticationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByCompanyName(String companyName);

    CompanyEntity findByCompanyName(String companyName);
}
