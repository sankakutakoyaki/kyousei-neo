package com.kyouseipro.neo.query.sql.common;

public class PushSqlBuilder {

    public static String buildFindByEndpoint() {
        return "SELECT * FROM subscriptions WHERE endpoint = ?";
    }

    public static String buildInsertSubscription() {
        return
            "INSERT INTO subscriptions (endpoint, p256dh, auth, username) VALUES (?,?,?,?);" +
            "DECLARE @NEW_ID int; SET @NEW_ID = @@IDENTITY;SELECT @NEW_ID as subscription_id;";
    }

    public static String buildFindAll() {
        return
            "SELECT * FROM subscriptions ";
    }

    public static String buildDeleteSubscriptionForEndpoint() {
        return
            "DELETE FROM subscriptions WHERE endpoint = ?;" +
            "DECLARE @ROW_COUNT int; SET @ROW_COUNT = @@ROWCOUNT;SELECT @ROW_COUNT as subscription_id;";
    }
}
