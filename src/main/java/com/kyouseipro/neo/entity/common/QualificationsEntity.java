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
        sb.append(" LEFT OUTER JOIN employees e ON e.employee_id = q.owner_id AND NOT (e.state = " + Enums.state.DELETE.getNum() + ")");
        sb.append(" LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = " + Enums.state.DELETE.getNum() + ")");
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
        sb.append(" LEFT OUTER JOIN companies c ON c.company_id = q.owner_id AND NOT (c.state = " + Enums.state.DELETE.getNum() + ") AND category = " + Enums.clientCategory.PARTNER.getNum());
        sb.append(" LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = " + Enums.state.DELETE.getNum() + ") AND qm.category_name = '許可'");
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
        sb.append(" AND NOT (q.state = " + Enums.state.DELETE.getNum() + ")");
        sb.append(" WHERE NOT (e.state = " + Enums.state.DELETE.getNum() + ")");
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
        sb.append(" AND NOT (q.state = " + Enums.state.DELETE.getNum() + ")");
        sb.append(" WHERE NOT (c.state = " + Enums.state.DELETE.getNum() + ") AND c.category = " + Enums.clientCategory.PARTNER.getNum() + " AND qm.category_name = '許可'");
        sb.append(" ORDER BY qm.qualification_master_id, c.company_id");
        return sb.toString();
    }

    public String getInsertString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        if (this.getAcquisition_date() == null) {
            this.setAcquisition_date(LocalDate.of(9999, 12, 31));
        }
        if (this.getExpiry_date() == null) {
            this.setExpiry_date(LocalDate.of(9999, 12, 31));
        }
        sb.append(logTable());
        sb.append("INSERT INTO qualifications (");
        sb.append("owner_id");                  sb2.append(this.getOwner_id());
        sb.append(", qualification_master_id"); sb2.append(", " + this.getQualification_master_id());
        sb.append(", number");                  sb2.append(", '" + this.getNumber() + "'");
        sb.append(", acquisition_date");        sb2.append(", '" + this.getAcquisition_date() + "'");
        sb.append(", expiry_date");             sb2.append(", '" + this.getExpiry_date() + "'");
        sb.append(")");                         sb2.append(");");
        sb.append(logString("新規"));
        sb.append(" VALUES (");sb.append(sb2.toString());
        sb.append("DECLARE @NEW_ID int;SET @NEW_ID = @@IDENTITY;");
        // 変更履歴
        sb.append("INSERT INTO qualifications_log SELECT * FROM @QualificationsTable;");
        // SimpleData
        sb.append(DatabaseService.getInsertLogTableString(this.getUser_name(), "qualifications", "作成"));
        // sb.append("IF @NEW_ID > 0 BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "qualifications", "作成成功", "@NEW_ID", ""));
        // sb.append("SELECT @NEW_ID as number, '作成しました' as text; END");
        // sb.append(" ELSE BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "qualifications", "作成失敗", "@NEW_ID", ""));
        // sb.append("SELECT 0 as number, '作成できませんでした' as text; END;");
        return sb.toString();
    }

    public String getUpdateString() {
        StringBuilder sb = new StringBuilder();
        if (this.getAcquisition_date() == null) {
            this.setAcquisition_date(LocalDate.of(9999, 12, 31));
        }
        if (this.getExpiry_date() == null) {
            this.setExpiry_date(LocalDate.of(9999, 12, 31));
        }
        sb.append(logTable());
        sb.append("UPDATE qualifications SET");
        sb.append(" owner_id = " + this.getOwner_id());
        sb.append(", qualification_master_id = " + this.getQualification_master_id());
        sb.append(", number = '" + this.getNumber() + "'");
        sb.append(", acquisition_date = '" + this.getAcquisition_date() + "'");
        sb.append(", expiry_date = '" + this.getExpiry_date() + "'");
        sb.append(", update_date = '" + LocalDateTime.now() + "'");
        int ver = this.getVersion() + 1;
        sb.append(", version = " + ver);
        sb.append(logString("更新"));
        sb.append(" WHERE qualifications_id = " + this.getQualifications_id()+ " AND version = " + this.getVersion() + ";");
        sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
        // 変更履歴
        sb.append("INSERT INTO qualifications_log SELECT * FROM @QualificationsTable;");
        // SimpleData
        sb.append(DatabaseService.getUpdateLogTableString(this.getUser_name(), "qualifications", "変更"));
        // sb.append("IF @ROW_COUNT > 0 BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "qualifications", "変更成功", "@ROW_COUNT", ""));
        // sb.append("SELECT 200 as number, '変更しました' as text; END");
        // sb.append(" ELSE BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "qualifications", "変更失敗", "@ROW_COUNT", ""));
        // sb.append("SELECT 0 as number, '変更できませんでした' as text; END;");
        return sb.toString();
    }

    public String getDeleteStringById(int id, String user_name) {
        StringBuilder sb = new StringBuilder();
        sb.append(logTable());
        sb.append("UPDATE qualifications SET");
        sb.append(" update_date = '" + LocalDateTime.now() + "'");
        sb.append(", state = " + Enums.state.DELETE.getNum());
        sb.append(logString("削除"));
        sb.append(" WHERE qualifications_id = " + id + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
        // 変更履歴
        sb.append("INSERT INTO qualifications_log SELECT * FROM @QualificationsTable;");
        // SimpleData
        sb.append(DatabaseService.getUpdateLogTableString(this.getUser_name(), "qualifications", "削除"));
        // sb.append("IF @ROW_COUNT > 0 BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "qualifications", "削除成功", "@ROW_COUNT", ""));
        // sb.append("SELECT 200 as number, '削除しました' as text; END");
        // sb.append(" ELSE BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "qualifications", "削除失敗", "@ROW_COUNT", ""));
        // sb.append("SELECT 0 as number, '削除できませんでした' as text; END;");
        return sb.toString();
    }

    public String logTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("DECLARE @QualificationsTable TABLE (");
        sb.append("editor NVARCHAR(255)");
        sb.append(", process NVARCHAR(50)");
        sb.append(", log_regist_date DATETIME2(7)");
        sb.append(", qualifications_id INT");
        sb.append(", owner_id INT");
        sb.append(", qualification_master_id INT");
        sb.append(", number NVARCHAR(255)");
        sb.append(", acquisition_date DATE");
        sb.append(", expiry_date DATE");
        sb.append(", regist_date DATE");
        sb.append(", update_date DATE");
        sb.append(", version INT");
        sb.append(", state INT");
        sb.append(");");
        return sb.toString();
    }

    public String logString(String process) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append(" OUTPUT");
        sb.append("'" + this.getUser_name() + "'");         sb2.append("editor");
        sb.append(", '" + process + "'");                   sb2.append(", process");
        sb.append(", CURRENT_TIMESTAMP");                   sb2.append(", log_regist_date");
        sb.append(", INSERTED.qualifications_id");          sb2.append(", qualifications_id");
        sb.append(", INSERTED.owner_id");                   sb2.append(", owner_id");
        sb.append(", INSERTED.qualification_master_id");    sb2.append(", qualification_master_id");
        sb.append(", INSERTED.number");                     sb2.append(", number");
        sb.append(", INSERTED.acquisition_date");           sb2.append(", acquisition_date");
        sb.append(", INSERTED.expiry_date");                sb2.append(", expiry_date");
        sb.append(", INSERTED.regist_date");                sb2.append(", regist_date");
        sb.append(", INSERTED.update_date");                sb2.append(", update_date");
        sb.append(", INSERTED.version");                    sb2.append(", version");
        sb.append(", INSERTED.state");                      sb2.append(", state");
        sb.append(" INTO @QualificationsTable (");          sb2.append(")");
        sb.append(sb2.toString());
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
    //         sb.append(entity.getNumber() + ",");
    //         sb.append(entity.getAcquisition_date() + ",");
    //         sb.append(entity.getExpiry_date() + ",");
    //         sb.append("\n"); // 改行を追加
    //     }
    //     return sb.toString();
    // }
}

