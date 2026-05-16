package com.kyouseipro.neo._backup.personnel.paidholiday.entity;

import lombok.Data;

@Data
public class PaidHolidayListEntity {
    private int employee_id;
    private String full_name;
    private int total_days;
}
