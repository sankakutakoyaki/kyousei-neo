package com.kyouseipro.neo.common.exception;

import java.security.Principal;
import java.util.Map;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kyouseipro.neo.entity.dto.ApiResponse;
import com.kyouseipro.neo.service.dto.HistoryService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final HistoryService historyService;

    // @ExceptionHandler(BusinessException.class)
    // @ResponseStatus(HttpStatus.OK)
    // public Map<String, Object> handleBusiness(BusinessException e) {
    //     return Map.of(
    //         "success", false,
    //         "message", e.getMessage()
    //     );
    // }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleSystem(Exception e) {
        return Map.of(
            "success", false,
            "message", "システムエラーが発生しました"
        );
    }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(
            BusinessException e,
            HttpServletRequest request
    ) {
        String table = (String) request.getAttribute("historyTable");
        String action = (String) request.getAttribute("historyAction");

        historyService.save(
            getUserName(request),
            table != null ? table : "unknown",
            action != null ? action : "不明",
            400,
            e.getMessage()
        );

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
    }

    private String getUserName(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        return principal != null ? principal.getName() : "unknown";
    }
}