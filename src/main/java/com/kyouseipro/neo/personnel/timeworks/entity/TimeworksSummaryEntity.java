package com.kyouseipro.neo.personnel.timeworks.entity;

import lombok.Data;

@Data
public class TimeworksSummaryEntity {
    private int employeeId;
    private String fullName;
    private int totalWorkingDate;
    private double totalWorkingTime;
}
