package com.kyouseipro.neo.entity.management.recycle;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RecycleDateEntity {
    private int recycle_id;
    private String recycle_number;
    private String molding_number;
    private LocalDate date;
}
