package com.kyouseipro.neo.entity.record;

import java.sql.ResultSet;
import java.time.LocalDateTime;

import com.kyouseipro.neo.interfaceis.IEntity;

import lombok.Data;

@Data
public class HistoryEntity implements IEntity {
    protected int history_id;
    protected LocalDateTime execution_date;
    protected String user_name;
    protected String table_name;
    protected String action;
    protected int result_code; // 2000番台以外はエラー
    protected String result_message;

    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.history_id = rs.getInt("history_id");
            this.execution_date = rs.getTimestamp("execution_date").toLocalDateTime();
            this.user_name = rs.getString("user_name");
            this.table_name = rs.getString("table_name");
            this.action = rs.getString("action");
            this.result_code = rs.getInt("result_code");
            this.result_message = rs.getString("result_messqage");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getInsertString() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO history (execution_date, user_name, table_name, action, result_code, result_message)");
        sb.append(" VALUES ('" + LocalDateTime.now() + "', '" + this.getUser_name() + "', '" + this.getTable_name() + "', '" + this.getAction() + "', " + this.result_code + ", '" + this.getResult_message() + "');");
        sb.append("SELECT 200 as number, '' as text;");
        return sb.toString();
    }

    /**
     * 作業履歴を保存するSQL文を発行する
     * @param user
     * @param table
     * @param action
     * @param number @NEW_ID 等を指定するために文字列で受け取る
     * @param text
     * @return
     */    
    public static String insertString(String user, String table, String action, String number, String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO history (execution_date, user_name, table_name, action, result_code, result_message)");
        sb.append(" VALUES ('" + LocalDateTime.now() + "', '" + user + "', '" + table + "', '" + action + "', " + number + ", '" + text + "');");
        return sb.toString();
    }
}
