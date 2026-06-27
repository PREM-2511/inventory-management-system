package com.stockiq.inventory.cron;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.stockiq.inventory.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteOldNotifications Cron Test")
class DeleteOldNotificationsTest {

  @Mock private NotificationService notificationService;

  @InjectMocks private DeleteOldNotifications deleteOldNotifications;

  @BeforeEach
  void setUp() {

    // Arrange (inject @Value fields)
    ReflectionTestUtils.setField(deleteOldNotifications, "deleteDays", 30);
    ReflectionTestUtils.setField(deleteOldNotifications, "batchSize", 100);
    ReflectionTestUtils.setField(deleteOldNotifications, "timeZone", "Asia/Colombo");
    ReflectionTestUtils.setField(deleteOldNotifications, "dateTimeFormat", "yyyy-MM-dd");
  }

  @Test
  @DisplayName("execute - deletes notifications in multiple batches")
  void execute_ReturnsDeleteNotificationsInBatches() throws Exception {
    // Arrange
    when(notificationService.countOldNotifications(anyString(), anyString(), anyString()))
        .thenReturn(250);

    // Act
    deleteOldNotifications.execute();

    // Assert / Verify
    verify(notificationService, times(1))
        .countOldNotifications(anyString(), anyString(), anyString());

    verify(notificationService, times(3))
        .deleteOldNotificationsBatch(anyString(), anyString(), anyString(), eq(100));
  }

  @Test
  @DisplayName("execute - no old notifications found")
  void execute_NoNotifications() throws Exception {
    // Arrange
    when(notificationService.countOldNotifications(anyString(), anyString(), anyString()))
        .thenReturn(0);

    // Act
    deleteOldNotifications.execute();

    // Assert / Verify
    verify(notificationService, times(1))
        .countOldNotifications(anyString(), anyString(), anyString());

    verify(notificationService, never())
        .deleteOldNotificationsBatch(anyString(), anyString(), anyString(), anyInt());
  }

  @Test
  @DisplayName("execute - exception during count does not crash job")
  void execute_ExceptionDuringCount() throws Exception {
    // Arrange
    when(notificationService.countOldNotifications(anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("DB error"));

    // Act (should NOT throw)
    deleteOldNotifications.execute();

    // Assert / Verify
    verify(notificationService, times(1))
        .countOldNotifications(anyString(), anyString(), anyString());

    verify(notificationService, never())
        .deleteOldNotificationsBatch(anyString(), anyString(), anyString(), anyInt());
  }

  @Test
  @DisplayName("execute - exception during batch delete")
  void execute_ExceptionDuringDelete() throws Exception {

    // Arrange
    when(notificationService.countOldNotifications(anyString(), anyString(), anyString()))
        .thenReturn(100);

    doThrow(new RuntimeException("Delete failed"))
        .when(notificationService)
        .deleteOldNotificationsBatch(anyString(), anyString(), anyString(), anyInt());

    // Act (should NOT crash cron job)
    deleteOldNotifications.execute();

    // Assert / Verify
    verify(notificationService, times(1))
        .countOldNotifications(anyString(), anyString(), anyString());

    verify(notificationService, times(1))
        .deleteOldNotificationsBatch(anyString(), anyString(), anyString(), eq(100));
  }
}
