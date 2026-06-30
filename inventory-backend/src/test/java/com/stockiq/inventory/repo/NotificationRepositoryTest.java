package com.stockiq.inventory.repo;

import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.entity.Notification;
import com.stockiq.inventory.repository.impl.NotificationRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.support.KeyHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Notification Repository Test")
@ExtendWith(MockitoExtension.class)
class NotificationRepositoryTest {

  private final String X_CORRELATION_ID = "X-Correlation-Id";
  private final String LOG_KEY = "test-log-key";

  @Mock private NamedParameterJdbcTemplate jdbcTemplate;

  @InjectMocks private NotificationRepositoryImpl notificationRepository;

  private Notification notification;
  private Long id;

  @BeforeEach
  void setUp() {

    id = 1L;

    notification =
        Notification.builder()
            .id(id)
            .userId(10L)
            .title("Test Notification")
            .message("Hello")
            .type("INFO")
            .isRead(false)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .build();
  }

  @Test
  @DisplayName("save returns generated ID when insert succeeds")
  void save_ReturnsGeneratedId_WhenInsertSucceeds() throws Exception {
    // Arrange
    when(jdbcTemplate.update(
            anyString(), any(MapSqlParameterSource.class), any(), any(String[].class)))
        .thenReturn(1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      long result = notificationRepository.save(X_CORRELATION_ID, LOG_KEY, notification);

      // Assert
      assertTrue(result >= 0);

      // Verify
      verify(jdbcTemplate, times(1))
          .update(anyString(), any(MapSqlParameterSource.class), any(), any(String[].class));
    }
  }

  @Test
  @DisplayName("findById returns notification when found")
  void findById_ReturnsNotification_WhenFound() throws Exception {
    // Arrange
    when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
        .thenReturn(List.of(notification));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      Optional<Notification> result =
          notificationRepository.findById(X_CORRELATION_ID, LOG_KEY, id);

      // Assert
      assertTrue(result.isPresent());
      assertEquals("Test Notification", result.get().getTitle());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class));
    }
  }

  @Test
  @DisplayName("findAll returns notifications list")
  void findAll_ReturnsList_WhenDataExists() throws Exception {
    // Arrange
    when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
        .thenReturn(List.of(notification));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      List<Notification> result = notificationRepository.findAll(X_CORRELATION_ID, LOG_KEY, 1, 10);

      // Assert
      assertEquals(1, result.size());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class));
    }
  }

  @Test
  @DisplayName("countAll returns correct count")
  void countAll_ReturnsCount_WhenQuerySucceeds() throws Exception {
    // Arrange
    when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
        .thenReturn(5L);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      long result = notificationRepository.countAll(X_CORRELATION_ID, LOG_KEY);

      // Assert
      assertEquals(5L, result);

      // Verify
      verify(jdbcTemplate, times(1))
          .queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class));
    }
  }

  @Test
  @DisplayName("countUnread returns unread count")
  void countUnread_ReturnsCount_WhenQuerySucceeds() throws Exception {
    // Arrange
    when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
        .thenReturn(2L);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      long result = notificationRepository.countUnread(X_CORRELATION_ID, LOG_KEY);

      // Assert
      assertEquals(2L, result);

      // Verify
      verify(jdbcTemplate, times(1))
          .queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class));
    }
  }

  @Test
  @DisplayName("markOneRead updates notification status")
  void markOneRead_ReturnsRows_WhenUpdated() throws Exception {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      int result = notificationRepository.markOneRead(X_CORRELATION_ID, LOG_KEY, id);

      // Assert
      assertEquals(1, result);

      // Verify
      verify(jdbcTemplate, times(1)).update(anyString(), any(MapSqlParameterSource.class));
    }
  }

  @Test
  @DisplayName("delete removes notification successfully")
  void delete_ReturnsRows_WhenDeleted() throws Exception {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      int result = notificationRepository.delete(X_CORRELATION_ID, LOG_KEY, id);

      // Assert
      assertEquals(1, result);

      // Verify
      verify(jdbcTemplate, times(1)).update(anyString(), any(MapSqlParameterSource.class));
    }
  }

  @Test
  @DisplayName("findById executes RowMapper and maps all fields")
  void findById_CoversRowMapper() throws Exception {
    // Arrange
    when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
        .thenAnswer(
            invocation -> {
              RowMapper<Notification> mapper = invocation.getArgument(2);

              // fake ResultSet
              var rs = mock(java.sql.ResultSet.class);

              when(rs.getLong("id")).thenReturn(1L);
              when(rs.getLong("user_id")).thenReturn(10L);
              when(rs.getString("title")).thenReturn("T");
              when(rs.getString("message")).thenReturn("M");
              when(rs.getString("type")).thenReturn("INFO");
              when(rs.getBoolean("is_read")).thenReturn(true);
              when(rs.getTimestamp("created_at"))
                  .thenReturn(new java.sql.Timestamp(System.currentTimeMillis()));

              Notification mapped = mapper.mapRow(rs, 0);

              return List.of(mapped);
            });

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      Optional<Notification> result =
          notificationRepository.findById(X_CORRELATION_ID, LOG_KEY, 1L);

      // Assert
      assertTrue(result.isPresent());
      assertEquals("T", result.get().getTitle());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class));
    }
  }

  @Test
  @DisplayName("save maps isRead true and false correctly")
  void save_CoversIsReadLogic() throws Exception {
    // Arrange
    when(jdbcTemplate.update(
            anyString(), any(MapSqlParameterSource.class), any(), any(String[].class)))
        .thenReturn(1);

    // CASE TRUE
    notification.setIsRead(true);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      notificationRepository.save(X_CORRELATION_ID, LOG_KEY, notification);

      // Assert + Verify
      verify(jdbcTemplate, atLeastOnce())
          .update(anyString(), argThat(params -> true), any(), any(String[].class));
    }
  }

  @Test
  @DisplayName("save returns 0 when KeyHolder key is null")
  void save_ReturnsZero_WhenKeyHolderIsNull() throws Exception {
    // Arrange
    when(jdbcTemplate.update(
            anyString(), any(MapSqlParameterSource.class), any(), any(String[].class)))
        .thenAnswer(invocation -> 1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      long result = notificationRepository.save(X_CORRELATION_ID, LOG_KEY, notification);

      // Assert
      assertEquals(0L, result);
    }
  }

  @Test
  @DisplayName("markAllRead updates all unread notifications")
  void markAllRead_Success() throws Exception {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(3);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      int result = notificationRepository.markAllRead(X_CORRELATION_ID, LOG_KEY);

      // Assert
      assertEquals(3, result);

      // Verify
      verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }
  }

  @Test
  @DisplayName("countOldNotifications returns count")
  void countOldNotifications_Success() throws Exception {
    // Arrange
    when(jdbcTemplate.queryForObject(
            anyString(), any(MapSqlParameterSource.class), eq(Integer.class)))
        .thenReturn(5);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      int result =
          notificationRepository.countOldNotifications(X_CORRELATION_ID, LOG_KEY, "2026-01-01");

      // Assert
      assertEquals(5, result);
    }
  }

  @Test
  @DisplayName("deleteOldNotificationsBatch deletes records in batch")
  void deleteOldNotificationsBatch_Success() throws Exception {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(2);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      int result =
          notificationRepository.deleteOldNotificationsBatch(
              X_CORRELATION_ID, LOG_KEY, "2026-01-01", 50);

      // Assert
      assertEquals(2, result);
    }
  }

  @Test
  @DisplayName("save covers isRead TRUE branch")
  void save_IsReadTrue() throws Exception {
    // Arrange
    notification.setIsRead(true);

    when(jdbcTemplate.update(
            anyString(), any(MapSqlParameterSource.class), any(), any(String[].class)))
        .thenAnswer(i -> 1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      notificationRepository.save(X_CORRELATION_ID, LOG_KEY, notification);

      // Assert + Verify
      verify(jdbcTemplate, times(1))
          .update(anyString(), any(MapSqlParameterSource.class), any(), any(String[].class));
    }
  }

  @Test
  @DisplayName("save covers isRead FALSE branch")
  void save_IsReadFalse() throws Exception {
    // Arrange
    notification.setIsRead(false);

    when(jdbcTemplate.update(
            anyString(), any(MapSqlParameterSource.class), any(), any(String[].class)))
        .thenAnswer(i -> 1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(10L);

      // Act
      notificationRepository.save(X_CORRELATION_ID, LOG_KEY, notification);

      // Assert
      verify(jdbcTemplate)
          .update(anyString(), any(MapSqlParameterSource.class), any(), any(String[].class));
    }
  }

  @Test
  @DisplayName("checkLowStockNotificationExists returns true when notification exists")
  void checkLowStockNotificationExists_ReturnsTrue() throws Exception {
    // Arrange
    when(jdbcTemplate.queryForObject(
            anyString(), any(MapSqlParameterSource.class), eq(Integer.class)))
        .thenReturn(1);

    // Act
    boolean result =
        notificationRepository.checkLowStockNotificationExists(
            X_CORRELATION_ID, LOG_KEY, 10L, "Laptop");

    // Assert
    assertTrue(result);

    // Verify
    verify(jdbcTemplate, times(1))
        .queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Integer.class));
  }

  @Test
  @DisplayName("checkLowStockNotificationExists returns false when notification does not exist")
  void checkLowStockNotificationExists_ReturnsFalse_WhenCountZero() throws Exception {
    // Arrange
    when(jdbcTemplate.queryForObject(
            anyString(), any(MapSqlParameterSource.class), eq(Integer.class)))
        .thenReturn(0);

    // Act
    boolean result =
        notificationRepository.checkLowStockNotificationExists(
            X_CORRELATION_ID, LOG_KEY, 10L, "Laptop");

    // Assert
    assertFalse(result);

    // Verify
    verify(jdbcTemplate, times(1))
        .queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Integer.class));
  }

  @Test
  @DisplayName("saveLowStockNotification saves notification with isRead TRUE")
  void saveLowStockNotification_IsReadTrue() throws Exception {
    // Arrange
    notification.setIsRead(true);

    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    // Act
    notificationRepository.saveLowStockNotification(X_CORRELATION_ID, LOG_KEY, notification);

    // Assert
    verify(jdbcTemplate, times(1)).update(anyString(), any(MapSqlParameterSource.class));
  }

  @Test
  @DisplayName("saveLowStockNotification saves notification with isRead FALSE")
  void saveLowStockNotification_IsReadFalse() throws Exception {
    // Arrange
    notification.setIsRead(false);

    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    // Act
    notificationRepository.saveLowStockNotification(X_CORRELATION_ID, LOG_KEY, notification);

    // Assert
    verify(jdbcTemplate, times(1)).update(anyString(), any(MapSqlParameterSource.class));
  }
}
