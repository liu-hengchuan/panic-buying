package com.example.myspringboot.exception;

import com.example.myspringboot.entity.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleException(Exception e) {
        log.error("Global exception caught: ", e);
        return Response.error("系统异常: " + e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument exception: ", e);
        return Response.error("参数错误: " + e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response handleIllegalStateException(IllegalStateException e) {
        log.error("Illegal state exception: ", e);
        return Response.error("状态错误: " + e.getMessage());
    }
}
