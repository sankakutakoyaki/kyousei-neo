package com.kyouseipro.neo._backup.personnel.timeworks.entity;

import com.kyouseipro.neo._backup.Enums;

import lombok.Data;

@Data
public class TimeworksRegistRequest {
    private TimeworksRequestDto dto;
    private Enums.timeworksCategory category;
}
