package com.kyouseipro.neo.common.exception;

public class BusinessException extends RuntimeException {
    private final int resultCode;

    public BusinessException(String message) {
        super(message);
        this.resultCode = 400;
    }

    public int getResultCode() {
        return resultCode;
    }
}
