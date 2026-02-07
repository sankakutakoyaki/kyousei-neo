package com.kyouseipro.neo.corporation.company.entity;

import lombok.Data;

@Data
public class CompanyEntityRequest {
    private Integer companyId;
    private Integer category;
    private String name;
    private String nameKana;
    private String telNumber;
    private String faxNumber;
    private String postalCode;
    private String fullAddress;
    private String email;
    private String webAddress;
    private Integer isOriginalPrice;
    private Integer version;
    private Integer state;
}
