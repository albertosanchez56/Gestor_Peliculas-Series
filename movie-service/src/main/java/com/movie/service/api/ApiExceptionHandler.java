// ApiExceptionHandler.java
package com.movie.service.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;

import jakarta.validation.ConstraintViolationException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

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

  /** TMDB devuelve 401 cuando la API key es inválida o es un token v4 enviado como api_key. */
  @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
  public ResponseEntity<Map<String, Object>> handleTmdbUnauthorized(HttpClientErrorException ex) {
    String body = ex.getResponseBodyAsString();
    String hint = "Comprueba que TMDB_API_KEY sea la API Key (v3) correcta. "
        + "Si usas un token de lectura v4, añade en config: tmdb.use-bearer-auth: true";
    if (body != null && (body.contains("Invalid API key") || body.contains("status_code\":7"))) {
      hint = "TMDB rechazó la API key. " + hint;
    }
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(Map.of(
            "timestamp", OffsetDateTime.now(),
            "status", 400,
            "message", hint
        ));
  }

  /** Circuit breaker abierto: TMDB no está disponible temporalmente. */
  @ExceptionHandler(CallNotPermittedException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public Map<String, Object> handleCircuitBreakerOpen(CallNotPermittedException ex) {
    return Map.of(
      "timestamp", OffsetDateTime.now(),
      "status", 503,
      "message", "Servicio de TMDB no disponible temporalmente. Inténtelo de nuevo en unos segundos."
    );
  }
}
