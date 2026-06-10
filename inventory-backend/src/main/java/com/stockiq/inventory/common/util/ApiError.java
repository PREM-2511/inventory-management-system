package com.stockiq.inventory.common.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiError {

  INTERNAL_SERVER_ERROR(
      "Internal Server Error",
      "INV0001",
      "Something went wrong. Internal server error",
      HttpStatus.INTERNAL_SERVER_ERROR),

  INPUT_DATA_VALIDATION_FAIL(
      "Invalid input data",
      "INV0002",
      "Validation Error",
      HttpStatus.BAD_REQUEST),

  CATEGORY_NOT_FOUND(
      "Category Not Found",
      "INV0003",
      "Category not found",
      HttpStatus.NOT_FOUND),

  DATA_INTEGRITY_VIOLATION_ON_DELETION_ERROR(
      "Deletion Error",
      "INV0004",
      "Can not delete this data, it is linked with other data",
      HttpStatus.BAD_REQUEST);

  private final String title;
  private final String code;
  private final String message;
  private final HttpStatus httpStatus;

  ApiError(String title, String code, String message, HttpStatus httpStatus) {
    this.title = title;
    this.code = code;
    this.message = message;
    this.httpStatus = httpStatus;
  }
}