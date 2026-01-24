package com.kyouseipro.neo.entity.dto;

import com.kyouseipro.neo.common.Enums;

import lombok.Data;

@Data
public class TimeworksRegistRequest {

    private TimeworksRequestDto dto;
    private Enums.timeworksCategory category;
}
