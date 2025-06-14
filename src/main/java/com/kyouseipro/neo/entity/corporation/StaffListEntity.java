package com.kyouseipro.neo.entity.corporation;

import lombok.Data;

@Data
public class StaffListEntity {
    private int staff_id;
    private int company_id;
    private int office_id;
    private String company_name;
    private String office_name;
    private String name;
    private String name_kana;
}