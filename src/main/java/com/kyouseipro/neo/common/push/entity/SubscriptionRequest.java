package com.kyouseipro.neo.common.push.entity;

import lombok.Data;

@Data
public class SubscriptionRequest {
    private int subscriptionId;
    private String endpoint;
    private String p256dh;
    private String auth;
    private String username;
}