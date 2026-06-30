package com.stockiq.inventory.common.util;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorMessage {

  // Error message description
  private String message;

  // Error title
  private String title;

  // HTTP status
  private HttpStatus error;

  // HTTP status code
  private int status;

  // Application error code
  private String code;

  // Error timestamp
  private String timestamp;

  // Additional error data
  private Object data;

  /**
   * Constructor for ErrorMessage.
   *
   * @param error ApiError
   */
  public ErrorMessage(ApiError error) {
    this.message = error.getMessage();
    this.title = error.getTitle();
    this.error = error.getHttpStatus();
    this.status = error.getHttpStatus().value();
    this.code = error.getCode();
    this.timestamp = LocalDateTime.now().toString();
  }

  /**
   * Get HTTP status code.
   *
   * @return int
   */
  public int getStatus() {
    return error.value();
  }

  /**
   * Set HTTP status code.
   *
   * @param status int
   */
  public void setStatus(int status) {
    this.status = status;
    this.error = HttpStatus.valueOf(status);
  }

  /**
   * Get current timestamp.
   *
   * @return String
   */
  public String getTimestamp() {
    return LocalDateTime.now().toString();
  }

  @Override
  public String toString() {
    return "ErrorMessage{"
        + "message='"
        + message
        + '\''
        + ", title='"
        + title
        + '\''
        + ", error="
        + error
        + ", status="
        + status
        + ", code='"
        + code
        + '\''
        + ", timestamp='"
        + timestamp
        + '\''
        + ", data="
        + data
        + '}';
  }
}
