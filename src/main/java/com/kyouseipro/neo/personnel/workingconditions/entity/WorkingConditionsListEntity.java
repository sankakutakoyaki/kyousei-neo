package com.kyouseipro.neo.personnel.workingconditions.entity;

import lombok.Data;

@Data
public class WorkingConditionsListEntity {
    private int workingConditionsId;
    private int employeeId;
    private int category;
    private String fullName;
    private String fullNameKana;
    private String officeName;
    private boolean exist = false;
}