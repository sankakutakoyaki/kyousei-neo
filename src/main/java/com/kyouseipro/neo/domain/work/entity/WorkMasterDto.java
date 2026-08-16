package com.kyouseipro.neo.domain.work.entity;

import java.math.BigDecimal;

public record WorkMasterDto(
    String workCode,
    String workName,
    BigDecimal workPrice
) {}