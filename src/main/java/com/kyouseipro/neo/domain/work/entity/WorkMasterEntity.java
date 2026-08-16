package com.kyouseipro.neo.domain.work.entity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class WorkMasterEntity {

    private long workMasterId;
    private String workCode;
    private String workName;
    private BigDecimal workPrice;
    private int state;
    private int version;
}