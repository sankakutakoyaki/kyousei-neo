package com.kyouseipro.neo.common.exception;

import java.security.Principal;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kyouseipro.neo.common.history.service.HistoryService;
import com.kyouseipro.neo.common.response.SimpleResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final HistoryService historyService;

    // @ExceptionHandler(Exception.class)
    // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    // public Map<String, Object> handleSystem(Exception e) {
    //     return Map.of(
    //         "success", false,
    //         "message", "システムエラーが発生しました"
    //     );
    // }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<SimpleResponse<Void>> handleBusiness(
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
                .body(new SimpleResponse(e.getMessage(), null));
    }

    private String getUserName(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        return principal != null ? principal.getName() : "unknown";
    }
}