package com.kyouseipro.neo.entity.person;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.interfaceis.IEntity;

import lombok.Data;

@Data
public class TimeworksListEntity implements IEntity {
    private int timeworks_id;
    private int employee_id;
    private int category;
    private String full_name;
    private String office_name;
    private LocalDate work_date;
    private String start_time;
    private String end_time;

    @Override
    public void setEntity(ResultSet rs) {
        try{
            this.timeworks_id = rs.getInt("timeworks_id");
            this.employee_id = rs.getInt("employee_id");
            this.category = rs.getInt("category");
            this.full_name = rs.getString("full_name");
            this.office_name = rs.getString("office_name");
            if (rs.getTimestamp("start_time") != null){
                LocalDateTime startTime = rs.getTimestamp("comp_start_time").toLocalDateTime();
                this.start_time = startTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString();
            }
            if (rs.getTimestamp("end_time") != null) {
                LocalDateTime endTime = rs.getTimestamp("comp_end_time").toLocalDateTime();
                this.end_time = endTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString();
            }
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    
    public String getSelectString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT t.*, e.category, COALESCE(o.name, '登録なし') as office_name, e.full_name From timeworks t");
        sb.append(" INNER JOIN employees e ON e.employee_id = t.employee_id");
        sb.append(" LEFT OUTER JOIN companies c ON c.company_id = e.company_id");
        sb.append(" LEFT OUTER JOIN offices o ON o.office_id = e.office_id");
        sb.append(" WHERE NOT (t.state = " + Enums.state.DELETE.getNum() + ")");
        return sb.toString();
    }
}
