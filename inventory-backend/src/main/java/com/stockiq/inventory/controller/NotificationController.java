package com.stockiq.inventory.controller;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.notification.NotificationType;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.notification.CreateNotificationResponse;
import com.stockiq.inventory.common.response.notification.DeleteNotificationResponse;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.NotificationDto;
import com.stockiq.inventory.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

  private static final String MAIN_LOG_KEY = "NOTIFICATION_";
  private static final String X_CORRELATION_ID = "X-Correlation-Id";
  private static final String INIT_LOG = "init_log";
  private static final String CONTROLLER = "controller";

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  /**
   * Create notification.
   *
   * @param notificationDto NotificationDto
   * @param correlationId String
   * @return ResponseEntity<CreateNotificationResponse>
   * @throws GeneralStatusCodeErrorException if an error occurs
   */
  @PostMapping
  public ResponseEntity<CreateNotificationResponse> create(
      @RequestBody @Valid NotificationDto notificationDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "CREATE";
    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    NotificationDto saved = notificationService.save(correlationId, logKey, notificationDto);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.CREATE.getValue()))
        .body(
            CreateNotificationResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .message(saved.getMessage())
                .type(saved.getType())
                .isRead(saved.getIsRead())
                .createdAt(saved.getCreatedAt())
                .build());
  }

  /**
   * Retrieve paginated list of notifications for the current user.
   *
   * @param correlationId request tracking id for logging
   * @param page page number
   * @param size page size
   * @return paginated notification list
   * @throws GeneralStatusCodeErrorException if retrieval fails
   */
  @GetMapping
  public ResponseEntity<PageResponse<NotificationDto>> list(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "FIND_ALL";
    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    PageResponse<NotificationDto> response =
        notificationService.findAll(correlationId, logKey, page, size);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(response);
  }

  /**
   * Get unread notification count for the current user.
   *
   * @param correlationId request tracking id for logging
   * @return unread notification count
   * @throws GeneralStatusCodeErrorException if operation fails
   */
  @GetMapping("/unread-count")
  public ResponseEntity<Long> getUnreadCount(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "UNREAD_COUNT";
    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    long count = notificationService.countUnread(correlationId, logKey);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(count);
  }

  /**
   * Mark a specific notification as read.
   *
   * @param id notification id
   * @param correlationId request tracking id for logging
   * @return HTTP 200 if successful
   * @throws GeneralStatusCodeErrorException if update fails
   */
  @PutMapping("/{id}/read")
  public ResponseEntity<Void> markAsRead(
      @PathVariable long id,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "MARK_READ";
    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    notificationService.markOneRead(correlationId, logKey, id);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue())).build();
  }

  /**
   * Mark all notifications as read for the current user.
   *
   * @param correlationId request tracking id for logging
   * @return HTTP 200 if successful
   * @throws GeneralStatusCodeErrorException if update fails
   */
  @PutMapping("/read-all")
  public ResponseEntity<Void> markAllAsRead(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "MARK_ALL_READ";
    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    notificationService.markAllRead(correlationId, logKey);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue())).build();
  }

  /**
   * Delete a notification by id for the current user.
   *
   * @param id notification id
   * @param correlationId request tracking id for logging
   * @return delete confirmation response
   * @throws GeneralStatusCodeErrorException if deletion fails
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<DeleteNotificationResponse> delete(
      @PathVariable long id,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "DELETE";
    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    notificationService.delete(correlationId, logKey, id);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(
            DeleteNotificationResponse.builder()
                .message("Notification deleted successfully")
                .build());
  }
}
