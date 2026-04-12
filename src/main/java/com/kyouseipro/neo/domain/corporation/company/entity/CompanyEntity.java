package com.kyouseipro.neo.domain.corporation.company.entity;

import lombok.Data;

@Data
public class CompanyEntity {
    private Long companyId;
    private int category;
    private String name;
    private String nameKana;
    private String telNumber;
    private String faxNumber;
    private String postalCode;
    private String fullAddress;
    private String email;
    private String webAddress;
    private int isOriginalPrice;
    private int version;
    private int state;
}
