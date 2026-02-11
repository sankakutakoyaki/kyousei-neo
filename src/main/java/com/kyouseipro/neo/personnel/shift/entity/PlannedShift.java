package com.kyouseipro.neo.personnel.shift.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PlannedShift {

    private Long plannedShiftId;
    private Long employeeId;
    private LocalDate shiftDate;
    private String shiftType;
}
