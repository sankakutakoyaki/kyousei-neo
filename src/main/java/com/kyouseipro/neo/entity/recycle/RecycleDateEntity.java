package com.kyouseipro.neo.entity.recycle;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RecycleDateEntity {
    private int recycleId;
    private String recycleNumber;
    private String moldingNumber;
    private LocalDate date;
}
