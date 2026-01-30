package com.kyouseipro.neo.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BetweenRequest {
    private int id;
    private LocalDate start;
    private LocalDate end;
    private String type;
}
