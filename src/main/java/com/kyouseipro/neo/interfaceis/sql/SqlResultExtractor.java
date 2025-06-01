package com.kyouseipro.neo.interfaceis.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlResultExtractor<R> {
    R extract(ResultSet rs) throws SQLException;
}