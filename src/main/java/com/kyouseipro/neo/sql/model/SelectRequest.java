package com.kyouseipro.neo.sql.model;

import java.util.Map;

import lombok.Data;

@Data
public class SelectRequest {
    private String queryId;
    private Map<String, Object> params;
}
