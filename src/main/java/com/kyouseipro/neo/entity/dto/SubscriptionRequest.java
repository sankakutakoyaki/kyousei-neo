package com.kyouseipro.neo.entity.dto;

import lombok.Data;

@Data
public class SubscriptionRequest {
    private int subscriptionId;
    private String endpoint;
    private String p256dh;
    private String auth;
    private String username;
}