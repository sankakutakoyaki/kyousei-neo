package com.kyouseipro.neo.interfaceis.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlParameterBinder<T> {
    void bind(PreparedStatement pstmt, T entity) throws SQLException;
}