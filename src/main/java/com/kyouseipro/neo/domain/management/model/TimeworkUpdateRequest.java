package com.kyouseipro.neo.domain.management.model;

import java.time.LocalDateTime;

public record TimeworkUpdateRequest(
    long timeworkId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    int version
) {
}
