package com.stockiq.inventory.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stockiq.inventory.common.enums.ErrorMessageCodeEnum;
import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.notification.NotificationType;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.notification.CreateNotificationResponse;
import com.stockiq.inventory.common.response.notification.DeleteNotificationResponse;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.NotificationDto;
import com.stockiq.inventory.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationController Test")
class NotificationControllerTest {

  private static final String X_CORRELATION_ID = "X-Correlation-Id";

  @Mock private NotificationService notificationService;

  @InjectMocks private NotificationController notificationController;

  private NotificationDto notificationDto;
  private NotificationDto savedNotification;
  private PageResponse<NotificationDto> pageResponse;

  @BeforeEach
  void setUp() {

    notificationDto = new NotificationDto();
    notificationDto.setId(1L);
    notificationDto.setUserId(1L);
    notificationDto.setTitle("Low Stock Alert");
    notificationDto.setMessage("Product quantity is low");
    notificationDto.setType(NotificationType.WARNING);
    notificationDto.setIsRead(false);
    notificationDto.setCreatedAt("2026-06-27 10:00:00");

    savedNotification = new NotificationDto();
    savedNotification.setId(1L);
    savedNotification.setUserId(1L);
    savedNotification.setTitle("Low Stock Alert");
    savedNotification.setMessage("Product quantity is low");
    savedNotification.setType(NotificationType.WARNING);
    savedNotification.setIsRead(false);
    savedNotification.setCreatedAt("2026-06-27 10:00:00");
  }

  @Test
  @DisplayName("Create Notification Success")
  void create_Success() throws Exception {
    // Arrange
    when(notificationService.save(anyString(), anyString(), any(NotificationDto.class)))
        .thenReturn(savedNotification);

    // Act
    ResponseEntity<CreateNotificationResponse> response =
        notificationController.create(notificationDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.CREATE.getValue(), response.getStatusCode().value());

    CreateNotificationResponse body = response.getBody();

    assertNotNull(body);
    assertEquals(1L, body.getId());
    assertEquals("Low Stock Alert", body.getTitle());
    assertEquals("Product quantity is low", body.getMessage());
    assertEquals(NotificationType.WARNING, body.getType());
    assertFalse(body.getIsRead());
    assertEquals("2026-06-27 10:00:00", body.getCreatedAt());

    // Verify
    verify(notificationService).save(anyString(), anyString(), any(NotificationDto.class));
  }

  @Test
  @DisplayName("Create Notification Throws Exception")
  void create_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Notification creation failed",
            "Create Notification Failed",
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
            ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
            new java.util.Date().toString(),
            null);

    when(notificationService.save(anyString(), anyString(), any(NotificationDto.class)))
        .thenThrow(new GeneralStatusCodeErrorException(errorMessage));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationController.create(notificationDto, X_CORRELATION_ID));

    // Verify
    verify(notificationService).save(anyString(), anyString(), any(NotificationDto.class));
  }

  @Test
  @DisplayName("List Notifications Success")
  void list_Success() throws Exception {
    // Arrange
    when(notificationService.findAll(anyString(), anyString(), anyInt(), anyInt()))
        .thenReturn(pageResponse);

    // Act
    ResponseEntity<PageResponse<NotificationDto>> response =
        notificationController.list(X_CORRELATION_ID, 1, 10);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(pageResponse, response.getBody());

    // Verify
    verify(notificationService).findAll(anyString(), anyString(), anyInt(), anyInt());
  }

  @Test
  @DisplayName("List Notifications Throws Exception")
  void list_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Notification list failed",
            "List Notification Failed",
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
            ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
            new java.util.Date().toString(),
            null);

    when(notificationService.findAll(anyString(), anyString(), anyInt(), anyInt()))
        .thenThrow(new GeneralStatusCodeErrorException(errorMessage));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationController.list(X_CORRELATION_ID, 1, 10));

    // Verify
    verify(notificationService).findAll(anyString(), anyString(), anyInt(), anyInt());
  }

  @Test
  @DisplayName("Get Unread Count Success")
  void getUnreadCount_Success() throws Exception {
    // Arrange
    when(notificationService.countUnread(anyString(), anyString())).thenReturn(5L);

    // Act
    ResponseEntity<Long> response = notificationController.getUnreadCount(X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(5L, response.getBody());

    // Verify
    verify(notificationService).countUnread(anyString(), anyString());
  }

  @Test
  @DisplayName("Get Unread Count Throws Exception")
  void getUnreadCount_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Unread count failed",
            "Unread Count Error",
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
            ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
            new java.util.Date().toString(),
            null);

    when(notificationService.countUnread(anyString(), anyString()))
        .thenThrow(new GeneralStatusCodeErrorException(errorMessage));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationController.getUnreadCount(X_CORRELATION_ID));

    // Verify
    verify(notificationService).countUnread(anyString(), anyString());
  }

  @Test
  @DisplayName("Mark Notification As Read Success")
  void markAsRead_Success() throws Exception {
    // Arrange
    doNothing().when(notificationService).markOneRead(anyString(), anyString(), anyLong());

    // Act
    ResponseEntity<Void> response = notificationController.markAsRead(1L, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());

    // Verify
    verify(notificationService).markOneRead(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Mark Notification As Read Throws Exception")
  void markAsRead_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Mark read failed",
            "Mark Read Error",
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
            ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
            new java.util.Date().toString(),
            null);

    doThrow(new GeneralStatusCodeErrorException(errorMessage))
        .when(notificationService)
        .markOneRead(anyString(), anyString(), anyLong());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationController.markAsRead(1L, X_CORRELATION_ID));

    // Verify
    verify(notificationService).markOneRead(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Mark All Notifications As Read Success")
  void markAllAsRead_Success() throws Exception {
    // Arrange
    doNothing().when(notificationService).markAllRead(anyString(), anyString());

    // Act
    ResponseEntity<Void> response = notificationController.markAllAsRead(X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());

    // Verify
    verify(notificationService).markAllRead(anyString(), anyString());
  }

  @Test
  @DisplayName("Mark All Notifications As Read Throws Exception")
  void markAllAsRead_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Mark all read failed",
            "Mark All Read Error",
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
            ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
            new java.util.Date().toString(),
            null);

    doThrow(new GeneralStatusCodeErrorException(errorMessage))
        .when(notificationService)
        .markAllRead(anyString(), anyString());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationController.markAllAsRead(X_CORRELATION_ID));

    // Verify
    verify(notificationService).markAllRead(anyString(), anyString());
  }

  @Test
  @DisplayName("Delete Notification Success")
  void delete_Success() throws Exception {
    // Arrange
    doNothing().when(notificationService).delete(anyString(), anyString(), anyLong());

    // Act
    ResponseEntity<DeleteNotificationResponse> response =
        notificationController.delete(1L, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());

    DeleteNotificationResponse body = response.getBody();

    assertNotNull(body);
    assertEquals("Notification deleted successfully", body.getMessage());

    // Verify
    verify(notificationService).delete(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Delete Notification Throws Exception")
  void delete_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Delete notification failed",
            "Delete Notification Error",
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
            ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
            new java.util.Date().toString(),
            null);

    doThrow(new GeneralStatusCodeErrorException(errorMessage))
        .when(notificationService)
        .delete(anyString(), anyString(), anyLong());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> notificationController.delete(1L, X_CORRELATION_ID));

    // Verify
    verify(notificationService).delete(anyString(), anyString(), eq(1L));
  }
}
