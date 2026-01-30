package com.kyouseipro.neo.corporation.office.entity;

import lombok.Data;

@Data
public class OfficeListEntity {
    private int officeId;
    private int companyId;
    private String name;
    private String nameKana;
    private String telNumber;
    private String email;   
}