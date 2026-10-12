package com.kyouseipro.neo.domain.master.work.model;

import java.math.BigDecimal;

public record WorkMasterDto(
    String workCode,
    String workName,
    BigDecimal workPrice
) {}