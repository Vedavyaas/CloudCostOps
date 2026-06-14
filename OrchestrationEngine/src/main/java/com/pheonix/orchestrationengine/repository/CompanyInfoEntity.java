package com.pheonix.orchestrationengine.repository;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class CompanyInfoEntity {
    private Long id;

    private String companyName;

    public CompanyInfoEntity() {
    }

    public CompanyInfoEntity(String companyName) {
        this.companyName = companyName;
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
}
