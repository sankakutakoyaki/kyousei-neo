package com.kyouseipro.neo.entity.data;

import java.sql.ResultSet;

import com.kyouseipro.neo.interfaceis.IEntity;

import lombok.Data;

@Data
public class SubscriptionRequest implements IEntity {
    private int subscription_id;
    private String endpoint;
    private String p256dh;
    private String auth;
    private String username;

    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.subscription_id = rs.getInt("subscription_id");
            this.endpoint = rs.getString("endpoint");
            this.p256dh = rs.getString("p256dh");
            this.auth = rs.getString("auth");
            this.username = rs.getString("username");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String getSelectString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM subscriptions");
        return sb.toString();
    }

    public String getInsertString() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO subscriptions (");
        sb.append("endpoint, p256dh, auth, username) VALUES (");
        sb.append("'" + this.endpoint + "', '" + this.p256dh + "', '" + this.auth + "', '" + this.username + "');");
        sb.append("DECLARE @NEW_ID int; SET @NEW_ID = @@IDENTITY;SELECT @NEW_ID as number;");
        return sb.toString();
    }
}