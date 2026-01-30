package com.kyouseipro.neo.personnel.timeworks.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

@Data
public class TimeworksRequestDto {

    @NotNull
    private Integer employeeId;

    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDt;

    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDt;

    private BigDecimal startLatitude;
    private BigDecimal startLongitude;
    private BigDecimal endLatitude;
    private BigDecimal endLongitude;

    private Integer breakMinutes;

    @JsonIgnore
    private LocalDate workBaseDate;
}
