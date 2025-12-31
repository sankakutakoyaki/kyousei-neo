package com.kyouseipro.neo.common.exception;

import java.sql.SQLException;

public final class SqlExceptionUtil {

    private SqlExceptionUtil() {}

    public static boolean isDuplicateKey(Throwable e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof SQLException sqlEx) {
                int code = sqlEx.getErrorCode();
                // SQL Server: 2601 = UNIQUE INDEX, 2627 = PK
                return code == 2601 || code == 2627;
            }
            cause = cause.getCause();
        }
        return false;
    }
}