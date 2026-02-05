package com.kyouseipro.neo.personnel.employee.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EmployeeEntityRequest {
    private Integer employeeId;
    private Integer companyId;
    private String companyName;
    private Integer officeId;
    private String officeName;
    private String account;
    private Integer code;
    private Integer category;
    private String lastName;
    private String firstName;
    private String fullName;
    private String lastNameKana;
    private String firstNameKana;
    private String fullNameKana;
    private String phoneNumber;
    private String postalCode;
    private String fullAddress;
    private String email;
    private Integer gender;
    private Integer bloodType;
    private LocalDate birthday;
    private String emergencyContact;
    private String emergencyContactNumber;
    private LocalDate dateOfHire;
    private Integer version;
    private Integer state;
}
