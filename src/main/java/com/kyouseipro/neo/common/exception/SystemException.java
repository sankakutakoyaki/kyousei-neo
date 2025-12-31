package com.kyouseipro.neo.common.exception;

public class SystemException extends RuntimeException {
    
    private final int resultCode = 500;

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getResultCode() {
        return resultCode;
    }
}