package com.stockiq.inventory.service;

import com.stockiq.inventory.common.enums.notification.NotificationType;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.dto.NotificationDto;
import com.stockiq.inventory.entity.Notification;
import com.stockiq.inventory.repository.NotificationRepository;
import com.stockiq.inventory.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  private static final String CORRELATION_ID = "corr-id";
  private static final String LOG_KEY = "log-key";

  @Mock private NotificationRepository notificationRepository;

  @InjectMocks private NotificationServiceImpl notificationService;

  private Notification notification;
  private NotificationDto notificationDto;

  @BeforeEach
  void setUp() {

    notification =
        Notification.builder()
            .id(1L)
            .userId(1L)
            .title("Low Stock")
            .message("Product stock is low")
            .type(NotificationType.INFO.name())
            .isRead(false)
            .createdAt(Timestamp.valueOf(LocalDateTime.now()))
            .build();

    notificationDto =
        NotificationDto.builder()
            .userId(1L)
            .title("Low Stock")
            .message("Product stock is low")
            .type(NotificationType.INFO)
            .build();
  }

  @Test
  @DisplayName("save notification success using security context")
  void save_ReturnsSuccess_UsingSecurityUser() throws Exception {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {

      securityUtils.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      when(notificationRepository.save(anyString(), anyString(), any(Notification.class)))
          .thenReturn(1L);

      // Act
      NotificationDto response = notificationService.save(CORRELATION_ID, LOG_KEY, notificationDto);

      // Assert
      assertNotNull(response);
      assertEquals(1L, response.getId());

      // Verify
      verify(notificationRepository, times(1))
          .save(anyString(), anyString(), any(Notification.class));
    }
  }

  @Test
  @DisplayName("save notification success using dto user id")
  void save_ReturnsSuccess_UsingDtoUser() throws Exception {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {

      securityUtils.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(null);

      when(notificationRepository.save(anyString(), anyString(), any(Notification.class)))
          .thenReturn(1L);

      // Act
      NotificationDto response = notificationService.save(CORRELATION_ID, LOG_KEY, notificationDto);

      // Assert
      assertNotNull(response);
      assertEquals(1L, response.getId());

      // Verify
      verify(notificationRepository, times(1))
          .save(anyString(), anyString(), any(Notification.class));
    }
  }

  @Test
  @DisplayName("save notification no user id throws exception")
  void save_NoUserId_ThrowsException() {
    // Arrange
    notificationDto.setUserId(null);

    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {

      securityUtils.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(null);

      // Act & Assert
      assertThrows(
          GeneralStatusCodeErrorException.class,
          () -> notificationService.save(CORRELATION_ID, LOG_KEY, notificationDto));
    }
  }

  @Test
  @DisplayName("save notification repository exception")
  void save_RepositoryException() throws Exception {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {

      securityUtils.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      when(notificationRepository.save(anyString(), anyString(), any(Notification.class)))
          .thenThrow(new RuntimeException("Database Error"));

      // Act & Assert
      assertThrows(
          GeneralStatusCodeErrorException.class,
          () -> notificationService.save(CORRELATION_ID, LOG_KEY, notificationDto));

      // Verify
      verify(notificationRepository, times(1))
          .save(anyString(), anyString(), any(Notification.class));
    }
  }

  @Test
  @DisplayName("find notification by id success")
  void findById_ReturnsSuccess() throws Exception {
    // Arrange
    when(notificationRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.of(notification));

    // Act
    NotificationDto response = notificationService.findById(CORRELATION_ID, LOG_KEY, 1L);

    // Assert
    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals(notification.getTitle(), response.getTitle());

    // Verify
    verify(notificationRepository, times(1)).findById(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("find notification by id not found")
  void findById_NotFound() throws Exception {
    // Arrange
    when(notificationRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationService.findById(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(notificationRepository, times(1)).findById(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("find notification by id generic exception")
  void findById_GenericException() throws Exception {
    // Arrange
    when(notificationRepository.findById(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("DB Error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationService.findById(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(notificationRepository, times(1)).findById(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("find all notifications success")
  void findAll_ReturnsSuccess() throws Exception {
    // Arrange
    when(notificationRepository.findAll(anyString(), anyString(), anyInt(), anyInt()))
        .thenReturn(List.of(notification));

    when(notificationRepository.countAll(anyString(), anyString())).thenReturn(1L);

    // Act
    PageResponse<NotificationDto> response =
        notificationService.findAll(CORRELATION_ID, LOG_KEY, 0, 10);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getContent().size());
    assertEquals(1L, response.getTotalElements());

    // Verify
    verify(notificationRepository, times(1)).findAll(anyString(), anyString(), anyInt(), anyInt());

    verify(notificationRepository, times(1)).countAll(anyString(), anyString());
  }

  @Test
  @DisplayName("find all notifications generic exception")
  void findAll_GenericException() throws Exception {
    // Arrange
    when(notificationRepository.findAll(anyString(), anyString(), anyInt(), anyInt()))
        .thenThrow(new RuntimeException("DB Error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationService.findAll(CORRELATION_ID, LOG_KEY, 0, 10));

    // Verify
    verify(notificationRepository, times(1)).findAll(anyString(), anyString(), anyInt(), anyInt());
  }

  @Test
  @DisplayName("find all notifications rethrows GeneralStatusCodeErrorException")
  void findAll_GeneralStatusCodeErrorException() throws Exception {
    // Arrange
    GeneralStatusCodeErrorException exception = mock(GeneralStatusCodeErrorException.class);

    when(exception.getMessage()).thenReturn("Notification Error");

    when(notificationRepository.findAll(anyString(), anyString(), anyInt(), anyInt()))
        .thenThrow(exception);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationService.findAll(CORRELATION_ID, LOG_KEY, 0, 10));

    // Verify
    verify(notificationRepository).findAll(anyString(), anyString(), anyInt(), anyInt());

    verify(exception).getMessage();
  }

  @Test
  @DisplayName("count unread notifications success")
  void countUnread_ReturnsSuccess() throws Exception {
    // Arrange
    when(notificationRepository.countUnread(anyString(), anyString())).thenReturn(5L);

    // Act
    long count = notificationService.countUnread(CORRELATION_ID, LOG_KEY);

    // Assert
    assertEquals(5L, count);

    // Verify
    verify(notificationRepository, times(1)).countUnread(anyString(), anyString());
  }

  @Test
  @DisplayName("count unread notifications generic exception")
  void countUnread_GenericException() throws Exception {
    // Arrange
    when(notificationRepository.countUnread(anyString(), anyString()))
        .thenThrow(new RuntimeException("DB Error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationService.countUnread(CORRELATION_ID, LOG_KEY));

    // Verify
    verify(notificationRepository, times(1)).countUnread(anyString(), anyString());
  }

  @Test
  @DisplayName("count unread notifications rethrows GeneralStatusCodeErrorException")
  void countUnread_GeneralStatusCodeErrorException() throws Exception {
    // Arrange
    GeneralStatusCodeErrorException exception = mock(GeneralStatusCodeErrorException.class);

    when(exception.getMessage()).thenReturn("Notification Error");

    when(notificationRepository.countUnread(anyString(), anyString())).thenThrow(exception);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationService.countUnread(CORRELATION_ID, LOG_KEY));

    // Verify
    verify(notificationRepository).countUnread(anyString(), anyString());

    verify(exception).getMessage();
  }

  @Test
  @DisplayName("mark one notification read success")
  void markOneRead_ReturnsSuccess() throws Exception {
    // Arrange
    when(notificationRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.of(notification));

    when(notificationRepository.markOneRead(anyString(), anyString(), anyLong())).thenReturn(1);

    // Act
    assertDoesNotThrow(() -> notificationService.markOneRead(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(notificationRepository, times(1)).findById(anyString(), anyString(), anyLong());

    verify(notificationRepository, times(1)).markOneRead(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("mark one notification read not found")
  void markOneRead_NotFound() throws Exception {
    // Arrange
    when(notificationRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationService.markOneRead(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(notificationRepository, times(1)).findById(anyString(), anyString(), anyLong());

    verify(notificationRepository, never()).markOneRead(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("mark one notification read generic exception")
  void markOneRead_GenericException() throws Exception {
    // Arrange
    when(notificationRepository.findById(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("DB Error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationService.markOneRead(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(notificationRepository, times(1)).findById(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("mark all notifications read success")
  void markAllRead_ReturnsSuccess() throws Exception {
    // Arrange
    when(notificationRepository.markAllRead(anyString(), anyString()))
        .thenReturn(1); // number of rows updated

    // Act + Assert
    assertDoesNotThrow(() -> notificationService.markAllRead(CORRELATION_ID, LOG_KEY));

    // Verify
    verify(notificationRepository, times(1)).markAllRead(anyString(), anyString());
  }

  @Test
  @DisplayName("mark all notifications read generic exception")
  void markAllRead_GenericException() throws Exception {
    // Arrange
    doThrow(new RuntimeException("DB Error"))
        .when(notificationRepository)
        .markAllRead(anyString(), anyString());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationService.markAllRead(CORRELATION_ID, LOG_KEY));

    // Verify
    verify(notificationRepository, times(1)).markAllRead(anyString(), anyString());
  }

  @Test
  @DisplayName("mark all notifications read rethrows GeneralStatusCodeErrorException")
  void markAllRead_GeneralStatusCodeErrorException() throws Exception {
    // Arrange
    GeneralStatusCodeErrorException exception = mock(GeneralStatusCodeErrorException.class);

    when(exception.getMessage()).thenReturn("Notification Error");

    doThrow(exception).when(notificationRepository).markAllRead(anyString(), anyString());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationService.markAllRead(CORRELATION_ID, LOG_KEY));

    // Verify
    verify(notificationRepository).markAllRead(anyString(), anyString());

    verify(exception).getMessage();
  }

  @Test
  @DisplayName("delete notification success")
  void delete_ReturnsSuccess() throws Exception {
    // Arrange
    when(notificationRepository.delete(anyString(), anyString(), anyLong())).thenReturn(1);

    // Act
    assertDoesNotThrow(() -> notificationService.delete(CORRELATION_ID, LOG_KEY, 1L));

    // Assert

    // Verify
    verify(notificationRepository, times(1)).delete(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("delete notification not found")
  void delete_NotFound() throws Exception {
    // Arrange
    when(notificationRepository.delete(anyString(), anyString(), anyLong())).thenReturn(0);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationService.delete(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(notificationRepository, times(1)).delete(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("delete notification generic exception")
  void delete_GenericException() throws Exception {
    // Arrange
    when(notificationRepository.delete(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("DB Error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationService.delete(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(notificationRepository, times(1)).delete(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("count old notifications success")
  void countOldNotifications_ReturnsSuccess() throws Exception {
    // Arrange
    when(notificationRepository.countOldNotifications(anyString(), anyString(), anyString()))
        .thenReturn(10);

    // Act
    int result = notificationService.countOldNotifications(CORRELATION_ID, LOG_KEY, "2024-01-01");

    // Assert
    assertEquals(10, result);

    // Verify
    verify(notificationRepository, times(1))
        .countOldNotifications(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("count old notifications exception")
  void countOldNotifications_Exception() throws Exception {
    // Arrange
    when(notificationRepository.countOldNotifications(anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("DB Error"));

    // Act & Assert
    assertThrows(
        RuntimeException.class,
        () -> notificationService.countOldNotifications(CORRELATION_ID, LOG_KEY, "2024-01-01"));

    // Verify
    verify(notificationRepository, times(1))
        .countOldNotifications(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("delete old notifications batch success")
  void deleteOldNotificationsBatch_ReturnsSuccess() throws Exception {
    // Arrange
    when(notificationRepository.deleteOldNotificationsBatch(
            anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(5);

    // Act
    int result =
        notificationService.deleteOldNotificationsBatch(CORRELATION_ID, LOG_KEY, "2024-01-01", 100);

    // Assert
    assertEquals(5, result);

    // Verify
    verify(notificationRepository, times(1))
        .deleteOldNotificationsBatch(anyString(), anyString(), anyString(), anyInt());
  }

  @Test
  @DisplayName("delete old notifications batch exception")
  void deleteOldNotificationsBatch_Exception() throws Exception {
    // Arrange
    when(notificationRepository.deleteOldNotificationsBatch(
            anyString(), anyString(), anyString(), anyInt()))
        .thenThrow(new RuntimeException("DB Error"));

    // Act & Assert
    assertThrows(
        RuntimeException.class,
        () ->
            notificationService.deleteOldNotificationsBatch(
                CORRELATION_ID, LOG_KEY, "2024-01-01", 100));

    // Verify
    verify(notificationRepository, times(1))
        .deleteOldNotificationsBatch(anyString(), anyString(), anyString(), anyInt());
  }

  @Test
  @DisplayName("checkAndCreateLowStockNotification does nothing when notification already exists")
  void checkAndCreateLowStockNotification_AlreadyExists() throws Exception {
    // Arrange
    when(notificationRepository.checkLowStockNotificationExists(
            anyString(), anyString(), anyLong(), anyString()))
        .thenReturn(true);

    // Act
    assertDoesNotThrow(
        () ->
            notificationService.checkAndCreateLowStockNotification(
                CORRELATION_ID, LOG_KEY, 1L, "Laptop", 5, 10));

    // Verify
    verify(notificationRepository, times(1))
        .checkLowStockNotificationExists(anyString(), anyString(), anyLong(), anyString());

    verify(notificationRepository, never())
        .saveLowStockNotification(anyString(), anyString(), any(Notification.class));
  }

  @Test
  @DisplayName("checkAndCreateLowStockNotification creates notification when none exists")
  void checkAndCreateLowStockNotification_CreateNotification() throws Exception {
    // Arrange
    when(notificationRepository.checkLowStockNotificationExists(
            anyString(), anyString(), anyLong(), anyString()))
        .thenReturn(false);

    doNothing()
        .when(notificationRepository)
        .saveLowStockNotification(anyString(), anyString(), any(Notification.class));

    // Act
    assertDoesNotThrow(
        () ->
            notificationService.checkAndCreateLowStockNotification(
                CORRELATION_ID, LOG_KEY, 1L, "Laptop", 5, 10));

    // Verify
    verify(notificationRepository)
        .checkLowStockNotificationExists(anyString(), anyString(), anyLong(), anyString());

    verify(notificationRepository)
        .saveLowStockNotification(anyString(), anyString(), any(Notification.class));
  }

  @Test
  @DisplayName("checkAndCreateLowStockNotification throws exception when repository fails")
  void checkAndCreateLowStockNotification_Exception() throws Exception {
    // Arrange
    when(notificationRepository.checkLowStockNotificationExists(
            anyString(), anyString(), anyLong(), anyString()))
        .thenThrow(new RuntimeException("DB Error"));

    // Act & Assert
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () ->
                notificationService.checkAndCreateLowStockNotification(
                    CORRELATION_ID, LOG_KEY, 1L, "Laptop", 5, 10));

    assertEquals("DB Error", exception.getMessage());

    // Verify
    verify(notificationRepository)
        .checkLowStockNotificationExists(anyString(), anyString(), anyLong(), anyString());

    verify(notificationRepository, never())
        .saveLowStockNotification(anyString(), anyString(), any(Notification.class));
  }
}
