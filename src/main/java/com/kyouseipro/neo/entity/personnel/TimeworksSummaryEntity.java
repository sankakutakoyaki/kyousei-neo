package com.kyouseipro.neo.entity.personnel;

import lombok.Data;

@Data
public class TimeworksSummaryEntity {
    private int employee_id;
    private String full_name;
    private int total_working_date;
    private int total_working_time;
}
