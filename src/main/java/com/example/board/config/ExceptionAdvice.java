package com.example.board.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(basePackages = "com.example.board")
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e) {
        log.error("exceptionHandler", e);
        return "error/common_error";
    }

}