package com.kyouseipro.neo.recycle.regist.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RecycleDateEntity {
    private int id;
    private String number;
    private String molding;
    private LocalDate date;
}
