package com.kyouseipro.neo.mapper.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.dto.SubscriptionRequest;

public class SubscriptionRequestMapper {
    public static SubscriptionRequest map(ResultSet rs) throws SQLException {
        SubscriptionRequest entity = new SubscriptionRequest();
        entity.setSubscription_id(rs.getInt("subscription_id"));
        entity.setEndpoint(rs.getString("endpoint"));
        entity.setP256dh(rs.getString("p256dh"));
        entity.setAuth(rs.getString("auth"));
        entity.setUsername(rs.getString("username"));
        return entity;
    }
}
