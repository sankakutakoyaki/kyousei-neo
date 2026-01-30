package com.kyouseipro.neo.common.push.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.common.push.entity.SubscriptionRequest;


public class SubscriptionRequestMapper {
    public static SubscriptionRequest map(ResultSet rs) throws SQLException {
        SubscriptionRequest entity = new SubscriptionRequest();
        entity.setSubscriptionId(rs.getInt("subscription_id"));
        entity.setEndpoint(rs.getString("endpoint"));
        entity.setP256dh(rs.getString("p256dh"));
        entity.setAuth(rs.getString("auth"));
        entity.setUsername(rs.getString("username"));
        return entity;
    }
}
