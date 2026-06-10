package com.stockiq.inventory.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActivityLog {

  /**
   * Log information.
   *
   * @param correlationId String
   * @param logKey String
   * @param logTag String
   * @param logMessage String
   */
  public static void logInfo(
      String correlationId, String logKey, String logTag, String logMessage) {

    log.info("{} | {} | {} | {}", logKey, correlationId, logTag, logMessage);
  }

  /**
   * Log error.
   *
   * @param correlationId String
   * @param logKey String
   * @param logTag String
   * @param logMessage String
   * @param error String
   */
  public static void logError(
      String correlationId, String logKey, String logTag, String logMessage, String error) {

    log.error("{} | {} | {} | {} | {}", logKey, correlationId, logTag, logMessage, error);
  }

  /**
   * Log API request/response.
   *
   * @param correlationId String
   * @param logKey String
   * @param logTag String
   * @param logMessage String
   * @param data Object
   */
  public static void apiLogInfo(
      String correlationId, String logKey, String logTag, String logMessage, Object data) {

    log.info("{} | {} | {} | {} | {}", logKey, correlationId, logTag, logMessage, data);
  }

  /**
   * Log API error.
   *
   * @param correlationId String
   * @param logKey String
   * @param logTag String
   * @param logMessage String
   * @param error String
   */
  public static void apiLogError(
      String correlationId, String logKey, String logTag, String logMessage, String error) {

    log.error("{} | {} | {} | {} | {}", logKey, correlationId, logTag, logMessage, error);
  }
}
