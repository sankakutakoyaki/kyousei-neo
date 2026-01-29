package com.kyouseipro.neo.entity.corporation;

import lombok.Data;

@Data
public class CompanyListEntity {
    private int companyId;
    private int category;
    private String name;
    private String nameKana;
    private String telNumber;
    private String email;    
}

