package com.stockiq.inventory.cron;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.service.NotificationService;
import com.stockiq.inventory.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("LowStockNotificationJob Cron Test")
class LowStockNotificationJobTest {

  @Mock private ProductService productService;
  @Mock private NotificationService notificationService;

  @InjectMocks private LowStockNotificationJob lowStockNotificationJob;

  @Test
  @DisplayName("execute - successfully checks and creates notifications for low stock products")
  void execute_success() throws Exception {
    // Arrange
    ProductDto product1 =
        ProductDto.builder().id(1L).userId(100L).name("Item A").qty(5).reorder(10).build();

    ProductDto product2 =
        ProductDto.builder().id(2L).userId(200L).name("Item B").qty(2).reorder(5).build();

    when(productService.getAllLowStockProducts(anyString(), anyString()))
        .thenReturn(List.of(product1, product2));

    // Act
    lowStockNotificationJob.execute();

    // Assert / Verify
    verify(productService, times(1)).getAllLowStockProducts(anyString(), anyString());
    verify(notificationService, times(1))
        .checkAndCreateLowStockNotification(
            anyString(), anyString(), eq(100L), eq("Item A"), eq(5), eq(10));
    verify(notificationService, times(1))
        .checkAndCreateLowStockNotification(
            anyString(), anyString(), eq(200L), eq("Item B"), eq(2), eq(5));
  }

  @Test
  @DisplayName("execute - product with null userId is skipped")
  void execute_nullUserId() throws Exception {
    // Arrange
    ProductDto product1 =
        ProductDto.builder().id(1L).userId(null).name("Item A").qty(5).reorder(10).build();

    when(productService.getAllLowStockProducts(anyString(), anyString()))
        .thenReturn(List.of(product1));

    // Act
    lowStockNotificationJob.execute();

    // Assert / Verify
    verify(productService, times(1)).getAllLowStockProducts(anyString(), anyString());
    verify(notificationService, never())
        .checkAndCreateLowStockNotification(
            anyString(), anyString(), anyLong(), anyString(), anyInt(), anyInt());
  }

  @Test
  @DisplayName("execute - service exception does not crash job")
  void execute_exception() throws Exception {
    // Arrange
    when(productService.getAllLowStockProducts(anyString(), anyString()))
        .thenThrow(new RuntimeException("Database down"));

    // Act & Assert (Should not throw)
    lowStockNotificationJob.execute();

    verify(productService, times(1)).getAllLowStockProducts(anyString(), anyString());
    verify(notificationService, never())
        .checkAndCreateLowStockNotification(
            anyString(), anyString(), anyLong(), anyString(), anyInt(), anyInt());
  }
}
