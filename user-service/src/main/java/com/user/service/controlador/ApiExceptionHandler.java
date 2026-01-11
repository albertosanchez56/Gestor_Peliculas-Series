package com.user.service.controlador;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
            "error", "Bad Request",
            "message", ex.getMessage()
        ));
    }
}
