package com.stockiq.inventory.repository;

import com.stockiq.inventory.entity.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

  /**
   * Insert a new notification into the database.
   *
   * @param correlationId request tracking id for logging
   * @param logKey logging context key
   * @param notification notification entity
   * @return generated notification id
   * @throws Exception if database operation fails
   */
  long save(String correlationId, String logKey, Notification notification) throws Exception;

  /**
   * Find notification by id for current user.
   *
   * @param correlationId request tracking id for logging
   * @param logKey logging context key
   * @param id notification id
   * @return optional notification
   * @throws Exception if query fails
   */
  Optional<Notification> findById(String correlationId, String logKey, long id) throws Exception;

  /**
   * Retrieve paginated notifications for current user.
   *
   * @param correlationId request tracking id for logging
   * @param logKey logging context key
   * @param page page number
   * @param size page size
   * @return list of notifications
   * @throws Exception if query fails
   */
  List<Notification> findAll(String correlationId, String logKey, int page, int size)
      throws Exception;

  /**
   * Count all notifications for current user.
   *
   * @param correlationId request tracking id for logging
   * @param logKey logging context key
   * @return total count
   */
  long countAll(String correlationId, String logKey) throws Exception;

  /**
   * Count unread notifications for current user.
   *
   * @param correlationId request tracking id for logging
   * @param logKey logging context key
   * @return unread count
   */
  long countUnread(String correlationId, String logKey) throws Exception;

  /**
   * Mark a notification as read.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  int markOneRead(String correlationId, String logKey, long id) throws Exception;

  /**
   * Mark all notifications as read.
   *
   * @param correlationId String
   * @param logKey String
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  int markAllRead(String correlationId, String logKey) throws Exception;

  /**
   * Delete Notification.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  int delete(String correlationId, String logKey, long id) throws Exception;

  /**
   * Count notifications older than cutoff date.
   *
   * @param correlationId String
   * @param logKey String
   * @param cutoffDate String
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  int countOldNotifications(String correlationId, String logKey, String cutoffDate)
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
      String correlationId, String logKey, String cutoffDate, int batchSize) throws Exception;

  /**
   * Check if an unread low-stock notification already exists for a given user and product name.
   *
   * @param correlationId String
   * @param logKey String
   * @param userId long
   * @param productName String
   * @return true if an unread notification exists, false otherwise
   * @throws Exception if an error occurs during the operation
   */
  boolean checkLowStockNotificationExists(
      String correlationId, String logKey, long userId, String productName) throws Exception;

  /**
   * Save a low-stock notification directly for a specific user (bypasses SecurityContext).
   *
   * @param correlationId String
   * @param logKey String
   * @param notification Notification
   * @throws Exception if an error occurs during the operation
   */
  void saveLowStockNotification(String correlationId, String logKey, Notification notification)
      throws Exception;
}
