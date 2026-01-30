package com.kyouseipro.neo.personnel.paidholiday.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PaidHolidayEntity {
    private int paid_holiday_id;
    private int employee_id;
    private String full_name;
    private LocalDate start_date;
    private LocalDate end_date;
    private int permit_employee_id;
    private String reason;
    private int version;
    private int state;
}
