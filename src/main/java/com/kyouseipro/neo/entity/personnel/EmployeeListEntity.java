package com.kyouseipro.neo.entity.personnel;

import lombok.Data;

@Data
public class EmployeeListEntity {
    private int employeeId;
    private int code;
    private int companyId;
    private String companyName;
    private int officeId;
    private String officeName;
    private String fullName;
    private String fullNameKana;
    private String phoneNumber;
    private int category;
    private String categoryName;
}
