package com.kyouseipro.neo.common.combo.entity;

import lombok.Data;

@Data
public class ComboDto {
    private Long parent;
    private Long value;
    private String label;

    public ComboDto(Long value, String label) {
        this.value = value;
        this.label = label;
    }

    public ComboDto(Long value, String label, Long parent) {
        this.value = value;
        this.label = label;
        this.parent = parent;
    }
}