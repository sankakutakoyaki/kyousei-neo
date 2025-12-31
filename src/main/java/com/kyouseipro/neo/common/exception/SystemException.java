package com.kyouseipro.neo.common.exception;

public class SystemException extends RuntimeException {

    public SystemException(Throwable cause) {
        super(cause);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}