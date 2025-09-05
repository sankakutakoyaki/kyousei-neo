package com.kyouseipro.neo.interfaceis.sql;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLBiConsumer<T, U> {
    void accept(T t, U u) throws SQLException;
}