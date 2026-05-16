package com.kyouseipro.neo._backup.corporation.staff.entity;

import lombok.Data;

@Data
public class StaffEntityRequest {
    private Integer staffId;
    private Integer companyId;
    private Integer officeId;
    private String companyName;
    private String officeName;
    private String name;
    private String nameKana;
    private String phoneNumber;
    private String email;
    private Integer version;
    private Integer state;
}
