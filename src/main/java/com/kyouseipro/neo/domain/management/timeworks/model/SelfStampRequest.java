package com.kyouseipro.neo.domain.management.timeworks.model;

// 本人打刻時の位置情報。取得できなかった場合はnullを許可する。
public record SelfStampRequest(
    String stampType,
    Double latitude,
    Double longitude,
    Double accuracy,
    java.time.LocalDateTime recordedAt
) {
}