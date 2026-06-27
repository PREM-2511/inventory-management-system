package com.stockiq.inventory.service;

import com.stockiq.inventory.common.enums.stock.MovementType;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.dto.StockDto;
import com.stockiq.inventory.entity.Product;
import com.stockiq.inventory.repository.NotificationRepository;
import com.stockiq.inventory.repository.ProductRepository;
import com.stockiq.inventory.repository.StockRepository;
import com.stockiq.inventory.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

  private static final String CORRELATION_ID = "corr-id";
  private static final String LOG_KEY = "log-key";
  private static final String USER_ID = "user-1";

  @Mock private ProductRepository productRepository;
  @Mock private StockRepository stockRepository;
  @Mock private NotificationRepository notificationRepository;

  @InjectMocks private StockServiceImpl stockService;

  private Product product;
  private StockDto stockDto;

  @BeforeEach
  void setUp() {

    product = Product.builder().id(1L).name("Laptop").qty(10).reorderPoint(3).userId(1L).build();

    stockDto = StockDto.builder().product("Laptop").qty(2).type(MovementType.IN).build();
  }

  @Test
  @DisplayName("log movement IN success")
  void logMovement_IN_Success() throws Exception {
    // Arrange
    when(productRepository.findByName(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(product));

    when(productRepository.update(anyString(), anyString(), any(Product.class))).thenReturn(1);

    when(stockRepository.save(anyString(), anyString(), any())).thenReturn(100L);

    // Act
    StockDto response = stockService.logMovement(CORRELATION_ID, LOG_KEY, stockDto, USER_ID);

    // Assert
    assertNotNull(response);
    assertEquals(100L, response.getId());

    // Verify
    verify(productRepository, times(1)).findByName(anyString(), anyString(), anyString());

    verify(productRepository, times(1)).update(anyString(), anyString(), any(Product.class));

    verify(stockRepository, times(1)).save(anyString(), anyString(), any());
  }

  @Test
  @DisplayName("log movement OUT success")
  void logMovement_OUT_Success() throws Exception {
    // Arrange
    stockDto.setType(MovementType.OUT);
    stockDto.setQty(2);

    when(productRepository.findByName(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(product));

    when(productRepository.update(anyString(), anyString(), any(Product.class))).thenReturn(1);

    when(stockRepository.save(anyString(), anyString(), any())).thenReturn(200L);

    // Act
    StockDto response = stockService.logMovement(CORRELATION_ID, LOG_KEY, stockDto, USER_ID);

    // Assert
    assertNotNull(response);
    assertEquals(200L, response.getId());

    // Verify
    verify(productRepository, times(1)).findByName(anyString(), anyString(), anyString());

    verify(productRepository, times(1)).update(anyString(), anyString(), any(Product.class));

    verify(stockRepository, times(1)).save(anyString(), anyString(), any());
  }

  @Test
  @DisplayName("log movement insufficient stock")
  void logMovement_InsufficientStock() throws Exception {
    // Arrange
    product.setQty(2);

    stockDto.setType(MovementType.OUT);
    stockDto.setQty(5);

    when(productRepository.findByName(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(product));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> stockService.logMovement(CORRELATION_ID, LOG_KEY, stockDto, USER_ID));

    // Verify
    verify(productRepository, times(1)).findByName(anyString(), anyString(), anyString());

    verify(productRepository, never()).update(anyString(), anyString(), any());
  }

  @Test
  @DisplayName("log movement product not found")
  void logMovement_ProductNotFound() throws Exception {
    // Arrange
    when(productRepository.findByName(anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> stockService.logMovement(CORRELATION_ID, LOG_KEY, stockDto, USER_ID));

    // Verify
    verify(productRepository, times(1)).findByName(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("log movement exception")
  void logMovement_Exception() throws Exception {
    // Arrange
    when(productRepository.findByName(anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> stockService.logMovement(CORRELATION_ID, LOG_KEY, stockDto, USER_ID));

    // Verify
    verify(productRepository, times(1)).findByName(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("get history success")
  void getHistory_Success() throws Exception {
    // Arrange
    StockDto dto = new StockDto();
    dto.setDate("2026-01-01T10:10:10.123");

    when(stockRepository.findAll(anyString(), anyString())).thenReturn(List.of(dto));

    // Act
    List<StockDto> result = stockService.getHistory(CORRELATION_ID, LOG_KEY);

    // Assert
    assertEquals(1, result.size());
    assertTrue(result.get(0).getDate().length() <= 19);

    // Verify
    verify(stockRepository, times(1)).findAll(anyString(), anyString());
  }

  @Test
  @DisplayName("get history exception")
  void getHistory_Exception() throws Exception {
    // Arrange
    when(stockRepository.findAll(anyString(), anyString()))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> stockService.getHistory(CORRELATION_ID, LOG_KEY));

    // Verify
    verify(stockRepository, times(1)).findAll(anyString(), anyString());
  }

  @Test
  @DisplayName("get low stock alerts success")
  void getLowStockAlerts_Success() throws Exception {
    // Arrange
    ProductDto dto = ProductDto.builder().qty(2).reorder(5).build();

    when(productRepository.getLowStockAlerts(anyString(), anyString())).thenReturn(List.of(dto));

    // Act
    List<ProductDto> result = stockService.getLowStockAlerts(CORRELATION_ID, LOG_KEY);

    // Assert
    assertEquals(1, result.size());
    assertNotNull(result.get(0).getStockLevel());

    // Verify
    verify(productRepository, times(1)).getLowStockAlerts(anyString(), anyString());
  }

  @Test
  @DisplayName("get low stock alerts exception")
  void getLowStockAlerts_Exception() throws Exception {
    // Arrange
    when(productRepository.getLowStockAlerts(anyString(), anyString()))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> stockService.getLowStockAlerts(CORRELATION_ID, LOG_KEY));

    // Verify
    verify(productRepository, times(1)).getLowStockAlerts(anyString(), anyString());
  }

  @Test
  @DisplayName("log movement IN - notification failure branch")
  void logMovement_IN_NotificationFailure() throws Exception {
    // Arrange
    when(productRepository.findByName(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(product));

    when(productRepository.update(anyString(), anyString(), any(Product.class))).thenReturn(1);

    when(stockRepository.save(anyString(), anyString(), any())).thenReturn(100L);

    // FORCE notification failure (this triggers missing line)
    doThrow(new RuntimeException("Notification DB error"))
        .when(notificationRepository)
        .save(anyString(), anyString(), any());

    // Act
    StockDto response = stockService.logMovement(CORRELATION_ID, LOG_KEY, stockDto, USER_ID);

    // Assert
    assertNotNull(response);
    assertEquals(100L, response.getId());

    // Verify main flow still executed
    verify(productRepository, times(1)).findByName(anyString(), anyString(), anyString());

    verify(notificationRepository, times(1)).save(anyString(), anyString(), any());
  }

  @Test
  @DisplayName("log movement OUT - low stock notification failure branch")
  void logMovement_OUT_LowStockNotificationFailure() throws Exception {
    // Arrange
    product.setQty(5);
    product.setReorderPoint(5); // ensures low stock condition triggers

    stockDto.setType(MovementType.OUT);
    stockDto.setQty(2);

    when(productRepository.findByName(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(product));

    when(productRepository.update(anyString(), anyString(), any(Product.class))).thenReturn(1);

    when(stockRepository.save(anyString(), anyString(), any())).thenReturn(200L);

    // FORCE notification failure
    doThrow(new RuntimeException("Notification DB error"))
        .when(notificationRepository)
        .save(anyString(), anyString(), any());

    // Act
    StockDto response = stockService.logMovement(CORRELATION_ID, LOG_KEY, stockDto, USER_ID);

    // Assert
    assertNotNull(response);
    assertEquals(200L, response.getId());

    // Verify notification was attempted
    verify(notificationRepository, times(1)).save(anyString(), anyString(), any());
  }
}
