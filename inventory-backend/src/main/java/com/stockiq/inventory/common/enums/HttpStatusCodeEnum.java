package com.stockiq.inventory.common.enums;

import lombok.Getter;

@Getter
// NOTE :- This enum is used to define HTTP status codes for API responses.
// This helps avoid hardcoding status code values throughout the application.
// It also provides a centralized location for commonly used HTTP status codes.
public enum HttpStatusCodeEnum {
  CREATE(201),
  OK(200),
  NO_CONTENT(204),

  BAD_REQUEST(400),
  UNAUTHORIZED(401),
  FORBIDDEN(403),
  NOT_FOUND(404),
  CONFLICT(409),

  INTERNAL_SERVER_ERROR(500);

  private final int value;

  HttpStatusCodeEnum(int value) {
    this.value = value;
  }
}
