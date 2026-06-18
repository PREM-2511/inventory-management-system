package com.stockiq.inventory.exception;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.exception.GeneralStatusCodeException;
import com.stockiq.inventory.common.util.ErrorMessage;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

  @ExceptionHandler(GeneralStatusCodeException.class)
  public ResponseEntity<ErrorMessage> generalStatusCodeException(GeneralStatusCodeException ex) {

    return ResponseEntity.status(ex.getApiError().getStatus())
        .header("X-Reason", "something-went-wrong")
        .body(ex.getApiError());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorMessage> handleValidationErrors(MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    String firstError = ex.getBindingResult().getFieldErrors().isEmpty()
        ? "Invalid input data"
        : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .header("X-Reason", "Bad Request")
        .body(
            new ErrorMessage(
                firstError,
                "Validation Error",
                HttpStatus.BAD_REQUEST,
                400,
                "INV0002",
                LocalDateTime.now().toString(),
                errors));
  }

  @ExceptionHandler(GeneralStatusCodeErrorException.class)
  public ResponseEntity<ErrorMessage> generalStatusCodeErrorException(
      GeneralStatusCodeErrorException ex) {

    return ResponseEntity.status(ex.getApiError().getStatus())
        .header("X-Reason", "something-went-wrong")
        .body(ex.getApiError());
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorMessage> handleRuntimeException(RuntimeException ex) {

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .header("X-Reason", "Bad Request")
        .body(
            new ErrorMessage(
                ex.getMessage(),
                "Bad Request",
                HttpStatus.BAD_REQUEST,
                400,
                "INV0002",
                LocalDateTime.now().toString(),
                null));
  }
}
