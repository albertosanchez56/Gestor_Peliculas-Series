// ApiExceptionHandler.java
package com.movie.service.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import jakarta.validation.ConstraintViolationException;

import java.time.OffsetDateTime;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,Object> handleBodyValidation(MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors = new LinkedHashMap<>();
    for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
      // si hay varios errores por campo, nos quedamos con el primero
      fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
    }
    return Map.of(
      "timestamp", OffsetDateTime.now(),
      "status", 400,
      "message", "Validación fallida",
      "errors", fieldErrors
    );
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,Object> handleConstraint(ConstraintViolationException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getConstraintViolations().forEach(v -> {
      String field = String.valueOf(v.getPropertyPath());
      errors.putIfAbsent(field, v.getMessage());
    });
    return Map.of(
      "timestamp", OffsetDateTime.now(),
      "status", 400,
      "message", "Validación de parámetros",
      "errors", errors
    );
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return Map.of(
      "timestamp", OffsetDateTime.now(),
      "status", 400,
      "message", "Tipo de dato inválido",
      "errors", Map.of(ex.getName(), "Valor inválido: " + ex.getValue())
    );
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,Object> handleIllegalArg(IllegalArgumentException ex) {
    return Map.of(
      "timestamp", OffsetDateTime.now(),
      "status", 400,
      "message", ex.getMessage()
    );
  }
}
