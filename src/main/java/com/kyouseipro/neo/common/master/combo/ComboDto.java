package com.kyouseipro.neo.common.master.combo;

import lombok.Data;

@Data
public class ComboDto {
    private Long value;
    private String label;

    public ComboDto(Long value, String label) {
        this.value = value;
        this.label = label;
    }
}