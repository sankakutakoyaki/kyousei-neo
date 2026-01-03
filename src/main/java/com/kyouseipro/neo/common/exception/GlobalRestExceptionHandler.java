package com.kyouseipro.neo.common.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class GlobalRestExceptionHandler {

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<?> handleSessionExpiredApi() {
        return ResponseEntity.status(401)
                .body(Map.of("success", false, "message", "session expired", "data", null));
    }
}