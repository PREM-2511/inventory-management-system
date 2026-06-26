package com.stockiq.inventory.repository.impl;

import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.entity.Notification;
import com.stockiq.inventory.repository.NotificationRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

  private static final String REPO_IMPL = "notification_repo_impl";

  private static final String INIT_LOG = "init_log";

  private static final String SELECT_ALL = "SELECT * FROM notifications";

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public NotificationRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<Notification> rowMapper =
      (rs, rowNum) ->
          Notification.builder()
              .id(rs.getLong("id"))
              .userId(rs.getLong("user_id"))
              .title(rs.getString("title"))
              .message(rs.getString("message"))
              .type(rs.getString("type"))
              .isRead(rs.getBoolean("is_read"))
              .createdAt(rs.getTimestamp("created_at"))
              .build();

  /**
   * Save Notification.
   *
   * @param correlationId String
   * @param logKey String
   * @param notification Notification
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public long save(String correlationId, String logKey, Notification notification)
      throws Exception {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        INSERT INTO notifications
        (
            user_id,
            title,
            message,
            type,
            is_read,
            created_at
        )
        VALUES
        (
            :userId,
            :title,
            :message,
            :type,
            :isRead,
            :createdAt
        )
        """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("userId", notification.getUserId())
            .addValue("title", notification.getTitle())
            .addValue("message", notification.getMessage())
            .addValue("type", notification.getType())
            .addValue(
                "isRead", notification.getIsRead() != null && notification.getIsRead() ? 1 : 0)
            .addValue("createdAt", notification.getCreatedAt());

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(sql, params, keyHolder, new String[] {"id"});

    return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0L;
  }

  /**
   * Find Notification By Id.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return Optional<Notification>
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public Optional<Notification> findById(String correlationId, String logKey, long id)
      throws Exception {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql = SELECT_ALL + " WHERE id = :id AND user_id = :userId";

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("id", id)
            .addValue("userId", SecurityUtils.getCurrentUserIdAsLong());

    List<Notification> notifications = jdbcTemplate.query(sql, params, rowMapper);

    return notifications.stream().findFirst();
  }

  /**
   * Find All Notifications with Pagination.
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @return List<Notification>
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public List<Notification> findAll(String correlationId, String logKey, int page, int size)
      throws Exception {
    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        SELECT * FROM notifications
        WHERE user_id = :userId
        ORDER BY created_at DESC
        LIMIT :limit OFFSET :offset
        """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("userId", SecurityUtils.getCurrentUserIdAsLong())
            .addValue("limit", size)
            .addValue("offset", Math.max(0, (page - 1) * size));

    return jdbcTemplate.query(sql, params, rowMapper);
  }

  /**
   * Count All Notifications.
   *
   * @param correlationId String
   * @param logKey String
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public long countAll(String correlationId, String logKey) throws Exception {
    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql = "SELECT COUNT(id) FROM notifications WHERE user_id = :userId";
    MapSqlParameterSource params =
        new MapSqlParameterSource().addValue("userId", SecurityUtils.getCurrentUserIdAsLong());

    Long count = jdbcTemplate.queryForObject(sql, params, Long.class);
    return count != null ? count : 0L;
  }

  /**
   * Count Unread Notifications.
   *
   * @param correlationId String
   * @param logKey String
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public long countUnread(String correlationId, String logKey) throws Exception {
    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql = "SELECT COUNT(id) FROM notifications WHERE user_id = :userId AND is_read = 0";
    MapSqlParameterSource params =
        new MapSqlParameterSource().addValue("userId", SecurityUtils.getCurrentUserIdAsLong());

    Long count = jdbcTemplate.queryForObject(sql, params, Long.class);
    return count != null ? count : 0L;
  }

  /**
   * Mark Notification As Read.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public int markOneRead(String correlationId, String logKey, long id) throws Exception {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        UPDATE notifications
        SET is_read = 1
        WHERE id = :id
          AND user_id = :userId
        """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("id", id)
            .addValue("userId", SecurityUtils.getCurrentUserIdAsLong());

    return jdbcTemplate.update(sql, params);
  }

  /**
   * Mark All Notifications As Read.
   *
   * @param correlationId String
   * @param logKey String
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public int markAllRead(String correlationId, String logKey) throws Exception {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        UPDATE notifications
        SET is_read = 1
        WHERE user_id = :userId
          AND is_read = 0
        """;

    MapSqlParameterSource params =
        new MapSqlParameterSource().addValue("userId", SecurityUtils.getCurrentUserIdAsLong());

    return jdbcTemplate.update(sql, params);
  }

  /**
   * Delete Notification.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public int delete(String correlationId, String logKey, long id) throws Exception {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        DELETE FROM notifications
        WHERE id = :id
          AND user_id = :userId
        """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("id", id)
            .addValue("userId", SecurityUtils.getCurrentUserIdAsLong());

    return jdbcTemplate.update(sql, params);
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
  public int countOldNotifications(
      String correlationId,
      String logKey,
      String cutoffDate)
      throws Exception {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        SELECT COUNT(id)
        FROM notifications
        WHERE user_id = :userId
          AND created_at < :cutoffDate
        """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("userId", SecurityUtils.getCurrentUserIdAsLong())
            .addValue("cutoffDate", cutoffDate);

    Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);

    return count != null ? count : 0;
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
      String correlationId,
      String logKey,
      String cutoffDate,
      int batchSize)
      throws Exception {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        DELETE FROM notifications
        WHERE user_id = :userId
          AND created_at < :cutoffDate
        LIMIT :limit
        """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("userId", SecurityUtils.getCurrentUserIdAsLong())
            .addValue("cutoffDate", cutoffDate)
            .addValue("limit", batchSize);

    return jdbcTemplate.update(sql, params);
  }
}
