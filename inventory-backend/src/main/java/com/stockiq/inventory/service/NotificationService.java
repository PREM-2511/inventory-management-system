package com.stockiq.inventory.service;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.NotificationDto;

public interface NotificationService {

  /**
   * Create and persist a new notification.
   *
   * @param correlationId request tracking id for logging
   * @param logKey logging context key
   * @param notificationDto notification data
   * @return created notification
   * @throws GeneralStatusCodeErrorException if save fails
   */
  NotificationDto save(String correlationId, String logKey, NotificationDto notificationDto)
      throws GeneralStatusCodeErrorException;

  /**
   * Retrieve a notification by its id for the current user.
   *
   * @param correlationId request tracking id for logging
   * @param logKey logging context key
   * @param id notification id
   * @return notification data
   * @throws GeneralStatusCodeErrorException if not found or access denied
   */
  NotificationDto findById(String correlationId, String logKey, long id)
      throws GeneralStatusCodeErrorException;

  /**
   * Retrieve paginated notifications for the current user.
   *
   * @param correlationId request tracking id for logging
   * @param logKey logging context key
   * @param page page number
   * @param size page size
   * @return paginated notifications
   * @throws GeneralStatusCodeErrorException if retrieval fails
   */
  PageResponse<NotificationDto> findAll(String correlationId, String logKey, int page, int size)
      throws GeneralStatusCodeErrorException;

  /**
   * Count unread notifications for the current user.
   *
   * @param correlationId request tracking id for logging
   * @param logKey logging context key
   * @return unread count
   */
  long countUnread(String correlationId, String logKey) throws GeneralStatusCodeErrorException;

/**
 * Mark a single notification as read.
 *
 * @param correlationId request tracking id for logging
 * @param logKey logging context key
 * @param id notification id
 */
  void markOneRead(String correlationId, String logKey, long id) throws GeneralStatusCodeErrorException;

  /**
   * Mark all notifications as read for the current user.
   *
   * @param correlationId request tracking id for logging
   * @param logKey logging context key
   */
  void markAllRead(String correlationId, String logKey) throws GeneralStatusCodeErrorException;

  /**
   * Delete a notification for the current user.
   *
   * @param correlationId request tracking id for logging
   * @param logKey logging context key
   * @param id notification id
   */
  void delete(String correlationId, String logKey, long id) throws GeneralStatusCodeErrorException;

  /**
   * Count notifications older than cutoff date.
   *
   * @param correlationId String
   * @param logKey String
   * @param cutoffDate String
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  int countOldNotifications(
      String correlationId,
      String logKey,
      String cutoffDate)
      throws Exception;

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
  int deleteOldNotificationsBatch(
      String correlationId,
      String logKey,
      String cutoffDate,
      int batchSize)
      throws Exception;
}
