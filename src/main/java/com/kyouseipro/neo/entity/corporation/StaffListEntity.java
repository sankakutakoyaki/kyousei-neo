package com.kyouseipro.neo.entity.corporation;

import lombok.Data;

@Data
public class StaffListEntity {
    private int staffId;
    private int companyId;
    private int officeId;
    private String companyName;
    private String officeName;
    private String name;
    private String nameKana;
}