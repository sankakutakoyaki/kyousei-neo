package com.kyouseipro.neo.common.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice(annotations = Controller.class)
public class GlobalControllerAdvice {

    @ExceptionHandler(SessionExpiredException.class)
    public String handleSessionExpired() {
        return "redirect:/";
    }
}