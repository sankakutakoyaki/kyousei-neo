package com.kyouseipro.neo.common.exception;

import java.util.Map;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> handleBusiness(BusinessException e) {
        return Map.of(
            "success", false,
            "message", e.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleSystem(Exception e) {
        return Map.of(
            "success", false,
            "message", "システムエラーが発生しました"
        );
    }
}