package com.kyouseipro.neo.corporation.office.entity;

import lombok.Data;

@Data
public class OfficeEntityRequest {
    private Integer officeId;
    private Integer companyId;
    private String companyName;
    private String name;
    private String nameKana;
    private String telNumber;
    private String faxNumber;
    private String postalCode;
    private String fullAddress;
    private String email;
    private String webAddress;
    private Integer version;
    private Integer state;
}
