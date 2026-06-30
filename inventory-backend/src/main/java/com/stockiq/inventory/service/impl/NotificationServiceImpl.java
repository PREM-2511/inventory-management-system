package com.stockiq.inventory.service.impl;

import com.stockiq.inventory.common.enums.ErrorMessageCodeEnum;
import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.notification.NotificationType;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.common.util.SystemFormats;
import com.stockiq.inventory.dto.NotificationDto;
import com.stockiq.inventory.entity.Notification;
import com.stockiq.inventory.repository.NotificationRepository;
import com.stockiq.inventory.service.NotificationService;
import java.time.format.DateTimeFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

  private static final String SERVICE_IMPL = "notification_service_impl";
  private static final String INIT_LOG = "init_log";
  private static final String EXCEPTION_LOG = "exception_log";

  private final NotificationRepository notificationRepository;

  public NotificationServiceImpl(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  /**
   * Create notification.
   *
   * @param correlationId String
   * @param logKey String
   * @param notificationDto NotificationDto
   * @return NotificationDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public NotificationDto save(String correlationId, String logKey, NotificationDto notificationDto)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      Notification notification = toEntity(notificationDto);

      // Resolve user id: prefer security context (authenticated call), fallback to DTO
      Long resolvedUserId = SecurityUtils.getCurrentUserIdAsLong();
      if (resolvedUserId == null || resolvedUserId == 0L) {
        resolvedUserId = notificationDto.getUserId();
      }

      if (resolvedUserId == null || resolvedUserId == 0L) {
        throw new GeneralStatusCodeErrorException(
            new ErrorMessage(
                "User ID could not be resolved — provide a valid JWT or userId in the request body",
                "Unauthorized",
                HttpStatus.UNAUTHORIZED,
                HttpStatusCodeEnum.UNAUTHORIZED.getValue(),
                ErrorMessageCodeEnum.USER_NOT_FOUND.getCode(),
                LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(SystemFormats.DATE_TIME_FORMAT_1)),
                null));
      }

      notification.setUserId(resolvedUserId);
      notification.setIsRead(false);
      notification.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

      long id = notificationRepository.save(correlationId, logKey, notification);

      notification.setId(id);

      return toDto(notification);

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to create notification",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.NOTIFICATION_CREATE_FAILED.getCode(),
              LocalDateTime.now()
                  .format(DateTimeFormatter.ofPattern(SystemFormats.DATE_TIME_FORMAT_1)),
              null));
    }
  }

  /**
   * Get notification by id.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return NotificationDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public NotificationDto findById(String correlationId, String logKey, long id)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      Notification notification =
          notificationRepository
              .findById(correlationId, logKey, id)
              .orElseThrow(
                  () ->
                      new GeneralStatusCodeErrorException(
                          new ErrorMessage(
                              "Notification not found",
                              "Notification Not Found",
                              HttpStatus.NOT_FOUND,
                              HttpStatusCodeEnum.NOT_FOUND.getValue(),
                              ErrorMessageCodeEnum.NOTIFICATION_NOT_FOUND.getCode(),
                              LocalDateTime.now()
                                  .format(
                                      DateTimeFormatter.ofPattern(
                                          SystemFormats.DATE_TIME_FORMAT_1)),
                              null)));

      return toDto(notification);

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to retrieve notification",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.NOTIFICATION_NOT_FOUND.getCode(),
              LocalDateTime.now()
                  .format(DateTimeFormatter.ofPattern(SystemFormats.DATE_TIME_FORMAT_1)),
              null));
    }
  }

  /**
   * Get all notifications.
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @return PageResponse<NotificationDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public PageResponse<NotificationDto> findAll(
      String correlationId, String logKey, int page, int size)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      List<Notification> notifications =
          notificationRepository.findAll(correlationId, logKey, page, size);

      List<NotificationDto> notificationDtos = notifications.stream().map(this::toDto).toList();

      long total = notificationRepository.countAll(correlationId, logKey);

      return new PageResponse<>(notificationDtos, page, size, total);

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to retrieve notifications",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.NOTIFICATION_NOT_FOUND.getCode(),
              LocalDateTime.now()
                  .format(DateTimeFormatter.ofPattern(SystemFormats.DATE_TIME_FORMAT_1)),
              null));
    }
  }

  /**
   * Count unread notifications.
   *
   * @param correlationId String
   * @param logKey String
   * @return long
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public long countUnread(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      return notificationRepository.countUnread(correlationId, logKey);

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to retrieve unread notification count",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.NOTIFICATION_NOT_FOUND.getCode(),
              LocalDateTime.now()
                  .format(DateTimeFormatter.ofPattern(SystemFormats.DATE_TIME_FORMAT_1)),
              null));
    }
  }

  /**
   * Mark notification as read.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public void markOneRead(String correlationId, String logKey, long id)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      // First verify the notification belongs to the current user
      notificationRepository
          .findById(correlationId, logKey, id)
          .orElseThrow(
              () ->
                  new GeneralStatusCodeErrorException(
                      new ErrorMessage(
                          "Notification not found",
                          "Notification Update Failed",
                          HttpStatus.NOT_FOUND,
                          HttpStatusCodeEnum.NOT_FOUND.getValue(),
                          ErrorMessageCodeEnum.NOTIFICATION_NOT_FOUND.getCode(),
                          LocalDateTime.now()
                              .format(
                                  DateTimeFormatter.ofPattern(SystemFormats.DATE_TIME_FORMAT_1)),
                          null)));

      // Mark as read (idempotent - already read notifications return 0 updated rows, which is fine)
      notificationRepository.markOneRead(correlationId, logKey, id);

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to mark notification as read",
              "Notification Update Failed",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.NOTIFICATION_UPDATE_FAILED.getCode(),
              LocalDateTime.now()
                  .format(DateTimeFormatter.ofPattern(SystemFormats.DATE_TIME_FORMAT_1)),
              null));
    }
  }

  /**
   * Mark all notifications as read.
   *
   * @param correlationId String
   * @param logKey String
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public void markAllRead(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      notificationRepository.markAllRead(correlationId, logKey);

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to mark all notifications as read",
              "Notification Update Failed",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.NOTIFICATION_UPDATE_FAILED.getCode(),
              LocalDateTime.now()
                  .format(DateTimeFormatter.ofPattern(SystemFormats.DATE_TIME_FORMAT_1)),
              null));
    }
  }

  /**
   * Delete notification.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public void delete(String correlationId, String logKey, long id)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      int deleted = notificationRepository.delete(correlationId, logKey, id);

      if (deleted == 0) {

        throw new GeneralStatusCodeErrorException(
            new ErrorMessage(
                "Notification not found",
                "Notification Delete Failed",
                HttpStatus.NOT_FOUND,
                HttpStatusCodeEnum.NOT_FOUND.getValue(),
                ErrorMessageCodeEnum.NOTIFICATION_NOT_FOUND.getCode(),
                LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(SystemFormats.DATE_TIME_FORMAT_1)),
                null));
      }

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to delete notification",
              "Notification Delete Failed",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.NOTIFICATION_DELETE_FAILED.getCode(),
              LocalDateTime.now()
                  .format(DateTimeFormatter.ofPattern(SystemFormats.DATE_TIME_FORMAT_1)),
              null));
    }
  }

  /**
   * Count notifications older than cutoff date.
   *
   * @param correlationId String
   * @param logKey String
   * @param cutoffDate String
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public int countOldNotifications(String correlationId, String logKey, String cutoffDate)
      throws Exception {

    ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

    try {

      return notificationRepository.countOldNotifications(correlationId, logKey, cutoffDate);

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;
    }
  }

  /**
   * Delete old notifications in batches.
   *
   * @param correlationId String
   * @param logKey String
   * @param cutoffDate String
   * @param batchSize int
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public int deleteOldNotificationsBatch(
      String correlationId, String logKey, String cutoffDate, int batchSize) throws Exception {

    ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

    try {

      return notificationRepository.deleteOldNotificationsBatch(
          correlationId, logKey, cutoffDate, batchSize);

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;
    }
  }

  /**
   * Check if an unread low-stock notification already exists and create one if not.
   *
   * @param correlationId String
   * @param logKey String
   * @param userId long
   * @param productName String
   * @param qty int
   * @param reorderPoint int
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public void checkAndCreateLowStockNotification(
      String correlationId,
      String logKey,
      long userId,
      String productName,
      int qty,
      int reorderPoint)
      throws Exception {

    ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

    try {

      boolean exists =
          notificationRepository.checkLowStockNotificationExists(
              correlationId, logKey, userId, productName);

      if (!exists) {

        String message =
            "Product '"
                + productName
                + "' has reached low stock level ("
                + qty
                + "/"
                + reorderPoint
                + "). Please reorder soon!";

        Notification notification =
            Notification.builder()
                .userId(userId)
                .title("Low Stock Warning")
                .message(message)
                .type(NotificationType.WARNING.name())
                .isRead(false)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        notificationRepository.saveLowStockNotification(correlationId, logKey, notification);
      }

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;
    }
  }

  private Notification toEntity(NotificationDto dto) {

    return Notification.builder()
        .id(dto.getId())
        .userId(dto.getUserId())
        .title(dto.getTitle())
        .message(dto.getMessage())
        .type(dto.getType() != null ? dto.getType().name() : NotificationType.INFO.name())
        .isRead(dto.getIsRead() != null ? dto.getIsRead() : false)
        .build();
  }

  private NotificationDto toDto(Notification entity) {

    return NotificationDto.builder()
        .id(entity.getId())
        .userId(entity.getUserId())
        .title(entity.getTitle())
        .message(entity.getMessage())
        .type(NotificationType.valueOf(entity.getType()))
        .isRead(entity.getIsRead())
        .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
        .build();
  }
}
