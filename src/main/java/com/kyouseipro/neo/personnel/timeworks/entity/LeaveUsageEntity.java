package com.kyouseipro.neo.personnel.timeworks.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LeaveUsageEntity {

    private Integer usageId;
    private Integer employeeId;
    private Integer timeworksId;

    private LocalDate usageDate;
    private Integer usedMinutes;

    private LocalDateTime createdAt;
}