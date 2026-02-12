package com.kyouseipro.neo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kyouseipro.neo.common.response.SimpleResponse;


@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<SimpleResponse<Void>> handleBusiness(BusinessException e) {
        return ResponseEntity
                .badRequest()
                .body(new SimpleResponse(e.getMessage(), null));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<SimpleResponse<Void>> handleSystem(SystemException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SimpleResponse("システムエラーが発生しました。", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SimpleResponse<Void>> handleOther(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SimpleResponse("予期しないエラーが発生しました。", null));
    }
}

