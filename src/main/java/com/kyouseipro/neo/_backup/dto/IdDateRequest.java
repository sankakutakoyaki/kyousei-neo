package com.kyouseipro.neo._backup.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class IdDateRequest {
    private int id;
    private LocalDate date;
}
