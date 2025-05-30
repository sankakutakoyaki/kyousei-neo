package com.kyouseipro.neo.entity.common;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.service.DatabaseService;

import lombok.Data;

@Data
public class QualificationsEntity implements Entity {
    private int qualifications_id;
    private int owner_id;
    private String owner_name;
    private String owner_name_kana;
    private int qualification_master_id;
    private String qualification_name;
    private String number;
    private LocalDate acquisition_date = LocalDate.of(9999, 12, 31);
    private LocalDate expiry_date = LocalDate.of(9999, 12, 31);
    private int version;
    private int state;

    private String status;
    private int is_enabled;
    private String user_name;

    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.qualifications_id = rs.getInt("qualifications_id");
            this.owner_id = rs.getInt("owner_id");
            this.owner_name = rs.getString("owner_name");
            this.owner_name_kana = rs.getString("owner_name_kana");
            this.qualification_master_id = rs.getInt("qualification_master_id");
            this.qualification_name = rs.getString("qualification_name");
            this.number = rs.getString("number");
            this.acquisition_date = rs.getDate("acquisition_date").toLocalDate();
            this.expiry_date = rs.getDate("expiry_date").toLocalDate();
            this.version = rs.getInt("version");
            this.state = rs.getInt("state");
            this.status = rs.getString("status");
            this.is_enabled = rs.getInt("is_enabled");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * セレクト用基本文字列
     * @return
     */
    public static String selectEmployeeString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT q.qualifications_id, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.is_enabled");
        sb.append(", q.version, q.state");
        sb.append(", e.full_name as owner_name, qm.name as qualification_name FROM qualifications q");
        sb.append(" LEFT OUTER JOIN employees e ON e.employee_id = q.owner_id AND NOT (e.state = " + Enums.state.DELETE.getCode() + ")");
        sb.append(" LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = " + Enums.state.DELETE.getCode() + ")");
        return sb.toString();
    }

    /**
     * セレクト用基本文字列
     * @return
     */
    public static String selectCompanyString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT q.qualifications_id, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.is_enabled");
        sb.append(", q.version, q.state");
        sb.append(", c.name as owner_name, qm.name as qualification_name FROM qualifications q");
        sb.append(" LEFT OUTER JOIN companies c ON c.company_id = q.owner_id AND NOT (c.state = " + Enums.state.DELETE.getCode() + ") AND category = " + Enums.clientCategory.PARTNER.getCode());
        sb.append(" LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = " + Enums.state.DELETE.getCode() + ") AND qm.category_name = '許可'");
        return sb.toString();
    }

    /**
     * セレクト用基本文字列
     * @return
     */
    public static String selectEmployeeStringByStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e.employee_id as owner_id, e.full_name as owner_name, e.full_name_kana as owner_name_kana, qm.name as qualification_name");
        sb.append(", q.qualifications_id, qm.qualification_master_id, q.number, q.is_enabled, q.version, q.state");
        sb.append(", COALESCE(q.acquisition_date, '9999-12-31') as acquisition_date, COALESCE(q.expiry_date, '9999-12-31') as expiry_date");
        sb.append(", CASE WHEN q.owner_id IS NOT NULL THEN '取得済み' ELSE '未取得' END AS status FROM employees e");
        sb.append(" CROSS JOIN qualification_master qm");
        sb.append(" LEFT JOIN qualifications q ON q.owner_id = e.employee_id AND q.qualification_master_id = qm.qualification_master_id");
        sb.append(" AND NOT (q.state = " + Enums.state.DELETE.getCode() + ")");
        sb.append(" WHERE NOT (e.state = " + Enums.state.DELETE.getCode() + ")");
        sb.append(" ORDER BY qm.qualification_master_id, e.employee_id");
        return sb.toString();
    }

    /**
     * セレクト用基本文字列
     * @return
     */
    public static String selectCompanyStringByStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT c.company_id as owner_id, c.name as owner_name, c.name_kana as owner_name_kana, qm.name as qualification_name");
        sb.append(", q.qualifications_id, qm.qualification_master_id, q.number, q.is_enabled, q.version, q.state");
        sb.append(", COALESCE(q.acquisition_date, '9999-12-31') as acquisition_date, COALESCE(q.expiry_date, '9999-12-31') as expiry_date");
        sb.append(", CASE WHEN q.owner_id IS NOT NULL THEN '取得済み' ELSE '未取得' END AS status FROM companies c");
        sb.append(" CROSS JOIN qualification_master qm");
        sb.append(" LEFT JOIN qualifications q ON q.owner_id = c.company_id AND q.qualification_master_id = qm.qualification_master_id");
        sb.append(" AND NOT (q.state = " + Enums.state.DELETE.getCode() + ")");
        sb.append(" WHERE NOT (c.state = " + Enums.state.DELETE.getCode() + ") AND c.category = " + Enums.clientCategory.PARTNER.getCode() + " AND qm.category_name = '許可'");
        sb.append(" ORDER BY qm.qualification_master_id, c.company_id");
        return sb.toString();
    }

    // public static String getCsvString(List<IEntity> items) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("所有者ID,");
    //     sb.append("所有者,");
    //     sb.append("資格名,");
    //     sb.append("資格番号,");
    //     sb.append("有効期限,");
    //     sb.append("取得日,");
    //     sb.append("\n");
    //     for (IEntity item : items) {
    //         QualificationsEntity entity = (QualificationsEntity) item;
    //         sb.append(entity.getowner_id() + ",");
    //         sb.append(entity.getowner_name() + ",");
    //         sb.append(entity.getQualification_name() + ",");
    //         sb.append(entity.getCodeber() + ",");
    //         sb.append(entity.getAcquisition_date() + ",");
    //         sb.append(entity.getExpiry_date() + ",");
    //         sb.append("\n"); // 改行を追加
    //     }
    //     return sb.toString();
    // }
}

