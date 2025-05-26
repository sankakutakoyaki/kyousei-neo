package com.kyouseipro.neo.entity.person;

import java.sql.ResultSet;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.record.HistoryEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.interfaceis.IFileUpload;
import com.kyouseipro.neo.service.DatabaseService;

import lombok.Data;

@Data
public class QualificationFilesEntity implements IEntity, IFileUpload {
    private int qualifications_files_id;
    private int qualifications_id;
    private String file_name;
    private String internal_name;
    private String folder_name;
    private int version;
    private int state;

    private String user_name;

    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.qualifications_files_id = rs.getInt("qualifications_files_id");
            this.qualifications_id = rs.getInt("qualifications_id");
            this.file_name = rs.getString("file_name");
            this.internal_name = rs.getString("internal_name");
            this.folder_name = rs.getString("folder_name");
            this.version = rs.getInt("version");
            this.state = rs.getInt("state");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public String getInsertString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append(logTable());
        sb.append("INSERT INTO qualifications_files (");
        sb.append("qualifications_id");             sb2.append(this.getQualifications_id());
        sb.append(", file_name");                   sb2.append(", '" + this.getFile_name() + "'");
        sb.append(", internal_name");               sb2.append(", '" + this.getInternal_name() + "'");
        sb.append(", folder_name");                 sb2.append(", '" + this.getFolder_name() + "'");
        sb.append(")");                             sb2.append(");");
        sb.append(logString("新規"));
        sb.append(" VALUES (");sb.append(sb2.toString());
        sb.append("DECLARE @NEW_ID int;SET @NEW_ID = @@IDENTITY;");
        // 変更履歴
        sb.append("INSERT INTO qualifications_files_log SELECT * FROM @QualificationsFilesTable;");
        // SimpleData
        sb.append(DatabaseService.getInsertLogTableString(this.getUser_name(), "qualifications_files", "作成"));
        // sb.append("IF @NEW_ID > 0 BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "qualifications_files", "作成成功", "@NEW_ID", ""));
        // sb.append("SELECT @NEW_ID as number, '" + this.getFile_name() + "' as text; END");
        // sb.append(" ELSE BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "qualifications_files", "作成失敗", "@NEW_ID", ""));
        // sb.append("SELECT 0 as number, '作成できませんでした' as text; END;");
        return sb.toString();
    }

    public String getDeleteString(String url) {
        StringBuilder sb = new StringBuilder();
        sb.append(logTable());
        sb.append("UPDATE qualifications_files SET");
        sb.append(" state = " + Enums.state.DELETE.getNum());
        int ver = this.getVersion() + 1;
        sb.append(", version = " + ver);
        sb.append(logString("削除"));
        sb.append(" WHERE folder_name = '" + url + "' AND version = " + this.getVersion() + ";");
        sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
        // 変更履歴
        sb.append("INSERT INTO qualifications_files_log SELECT * FROM @QualificationsFilesTable;");
        // SimpleData
        sb.append(DatabaseService.getUpdateLogTableString(this.getUser_name(), "qualifications_files", "削除"));
        // sb.append("IF @ROW_COUNT > 0 BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "qualifications_files", "削除成功", "@ROW_COUNT", ""));
        // sb.append("SELECT 200 as number, '削除しました' as text; END");
        // sb.append(" ELSE BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "qualifications_files", "削除失敗", "@ROW_COUNT", ""));
        // sb.append("SELECT 0 as number, '削除できませんでした' as text; END;");
        return sb.toString();
    }

    public String logTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("DECLARE @QualificationsFilesTable TABLE (");
        sb.append("editor NVARCHAR(255)");
        sb.append(", process NVARCHAR(50)");
        sb.append(", log_regist_date DATETIME2(7)");
        sb.append(", qualifications_files_id INT");
        sb.append(", qualifications_id INT");
        sb.append(", file_name NVARCHAR(255)");
        sb.append(", internal_name NVARCHAR(255)");
        sb.append(", folder_name NVARCHAR(255)");
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
        sb.append(", INSERTED.qualifications_files_id");    sb2.append(", qualifications_files_id");
        sb.append(", INSERTED.qualifications_id");          sb2.append(", qualifications_id");
        sb.append(", INSERTED.file_name");                  sb2.append(", file_name");
        sb.append(", INSERTED.internal_name");              sb2.append(", internal_name");
        sb.append(", INSERTED.folder_name");                sb2.append(", folder_name");
        sb.append(", INSERTED.version");                    sb2.append(", version");
        sb.append(", INSERTED.state");                      sb2.append(", state");
        sb.append(" INTO @QualificationsFilesTable (");          sb2.append(")");
        sb.append(sb2.toString());
        return sb.toString();
    }

    @Override
    public void setFileName(String file_name) {
        this.file_name = file_name;
    }

    @Override
    public void setInternalName(String internal_name) {
        this.internal_name = internal_name;
    }

    @Override
    public void setFolderName(String folder_name) {
        this.folder_name = folder_name;
    }
}
