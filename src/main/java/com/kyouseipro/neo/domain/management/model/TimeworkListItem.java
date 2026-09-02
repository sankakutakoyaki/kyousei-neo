package com.kyouseipro.neo.domain.management.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TimeworkListItem(
    long timeworkId, long employeeId, String fullName, Long officeId, String officeName,
    LocalDate workDate, LocalDateTime startTime, LocalDateTime endTime, int version
) {
}
