package com.kyouseipro.neo.corporation.company.dto;

import lombok.Data;

@Data
public class CompanyListResponse {
    private Long companyId;
    private int category;
    private String name;
    private String nameKana;
    private String telNumber;
    private String email;    
}

