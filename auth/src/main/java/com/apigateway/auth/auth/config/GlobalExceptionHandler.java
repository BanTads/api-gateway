package com.apigateway.auth.auth.config;

import com.apigateway.auth.auth.utils.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        String errorMessage = "Método não permitido para esta solicitação.";
        return new ResponseEntity<>(new Response(false, errorMessage, null), HttpStatus.METHOD_NOT_ALLOWED);
    }
}