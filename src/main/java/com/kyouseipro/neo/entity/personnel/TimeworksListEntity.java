package com.kyouseipro.neo.entity.personnel;

import java.time.LocalDate;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class TimeworksListEntity implements CsvExportable {
    private int timeworks_id;
    private int employee_id;
    private int category;
    private String full_name;
    private String office_name;
    private LocalDate work_date;
    private String start_time;
    private String end_time;
    private String comp_start_time;
    private String comp_end_time;
    private String basic_start_time;
    private String basic_end_time;
    private String rest_time;
    // private int paid_holiday_id;
    // private String work_style;
    private String situation;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,従業員名,出勤日,出勤時刻(打刻),退勤時刻(打刻),出勤時刻(確定),退勤時刻(確定),休憩時間,状態,営業所";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(employee_id)) + "," +
               Utilities.escapeCsv(full_name) + "," +
               Utilities.escapeCsv(String.valueOf(work_date)) + "," +
               Utilities.escapeCsv(start_time) + "," +
               Utilities.escapeCsv(end_time) + "," +
               Utilities.escapeCsv(comp_start_time) + "," +
               Utilities.escapeCsv(comp_end_time) + "," +
               Utilities.escapeCsv(rest_time) + "," +
               Utilities.escapeCsv(situation) + "," +
               Utilities.escapeCsv(office_name);
    }

    // @Override
    // public void setEntity(ResultSet rs) {
    //     try{
    //         this.timeworks_id = rs.getInt("timeworks_id");
    //         this.employee_id = rs.getInt("employee_id");
    //         this.category = rs.getInt("category");
    //         this.full_name = rs.getString("full_name");
    //         this.office_name = rs.getString("office_name");
    //         if (rs.getTimestamp("start_time") != null){
    //             LocalDateTime startTime = rs.getTimestamp("comp_start_time").toLocalDateTime();
    //             this.start_time = startTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString();
    //         }
    //         if (rs.getTimestamp("end_time") != null) {
    //             LocalDateTime endTime = rs.getTimestamp("comp_end_time").toLocalDateTime();
    //             this.end_time = endTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString();
    //         }
    //     } catch(Exception e) {
    //         System.out.println(e);
    //     }
    // }
    
    // public String getSelectString() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT t.*, e.category, COALESCE(o.name, '登録なし') as office_name, e.full_name From timeworks t");
    //     sb.append(" INNER JOIN employees e ON e.employee_id = t.employee_id");
    //     sb.append(" LEFT OUTER JOIN companies c ON c.company_id = e.company_id");
    //     sb.append(" LEFT OUTER JOIN offices o ON o.office_id = e.office_id");
    //     sb.append(" WHERE NOT (t.state = " + Enums.state.DELETE.getCode() + ")");
    //     return sb.toString();
    // }
}
