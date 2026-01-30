package com.kyouseipro.neo.personnel.timeworks.entity;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kyouseipro.neo.common.Enums;

import lombok.Data;

@Data
public class TimeworksEntity {

    private Integer timeworksId;
    private Integer employeeId;
    private String fullName;
    private String officeName;

    private LocalDateTime startDt;
    private LocalDateTime endDt;

    private Integer breakMinutes;

    private LocalDate workBaseDate;

    private BigDecimal startLatitude;
    private BigDecimal startLongitude;
    private BigDecimal endLatitude;
    private BigDecimal endLongitude;

    private Enums.timeworksType workType;
    private Enums.timeworksState state;

    private LocalDateTime registDate;
    private LocalDateTime updateDate;

    /* ===== 業務ロジック ===== */

    /** 勤務時間（分） */
    public Integer calcWorkMinutes() {
        if (startDt == null || endDt == null) return null;
        return (int) Duration.between(startDt, endDt).toMinutes() - breakMinutes;
    }

    /** 打刻中か */
    public boolean isWorking() {
        return state != null && state.getCode() == Enums.timeworksState.WORKING.getCode();
    }
}