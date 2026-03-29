package com.kyouseipro.neo.interfaces;

import java.util.List;
import java.util.Map;

public interface LogSqlProvider {

    String buildLogTable(String tableVar);

    String buildOutput();

    String buildInsertLog(String tableVar, String action);

    List<Object> buildLogParams(Map<String, Object> req, String action);
}