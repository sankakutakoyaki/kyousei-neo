package com.kyouseipro.neo.sql.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoundSql {
    private final String sql;
    private final List<Object> params;
}