package com.kyouseipro.neo.domain.management.timeworks.model;

import java.time.LocalDateTime;

public record TimeworkUpdateRequest(
    long timeworkId,
    Long timeworkEditId,
    LocalDateTime editStartTime,
    LocalDateTime editEndTime
) {
}
