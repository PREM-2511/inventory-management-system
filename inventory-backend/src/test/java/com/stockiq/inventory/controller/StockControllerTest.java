package com.stockiq.inventory.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.stock.MovementType;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.stock.LogMovementResponse;
import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.dto.StockDto;
import com.stockiq.inventory.service.StockService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockController Test")
class StockControllerTest {

  private static final String X_CORRELATION_ID = "X-Correlation-Id";
  private static final String X_USER_EMAIL = "X-User-Email";

  @Mock private StockService stockService;

  @InjectMocks private StockController stockController;

  private StockDto stockDto;
  private StockDto savedStock;

  @BeforeEach
  void setUp() {

    stockDto =
        StockDto.builder()
            .id(1L)
            .type(MovementType.IN)
            .product("Laptop")
            .qty(10)
            .date("2026-06-27 10:00:00")
            .user("admin")
            .build();

    savedStock =
        StockDto.builder()
            .id(1L)
            .type(MovementType.IN)
            .product("Laptop")
            .qty(10)
            .date("2026-06-27 10:00:00")
            .user("admin")
            .build();
  }

  @Test
  @DisplayName("Log Movement Success")
  void logMovement_Success() throws GeneralStatusCodeErrorException {
    // Arrange
    when(stockService.logMovement(anyString(), anyString(), any(StockDto.class), anyString()))
        .thenReturn(savedStock);

    try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {

      mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn("1");

      // Act
      ResponseEntity<LogMovementResponse> response =
          stockController.logMovement(stockDto, X_CORRELATION_ID, X_USER_EMAIL);

      // Assert
      assertEquals(HttpStatusCodeEnum.CREATE.getValue(), response.getStatusCode().value());

      LogMovementResponse body = response.getBody();
      assertNotNull(body);
      assertEquals(1L, body.getId());
      assertEquals("Laptop", body.getProduct());
      assertEquals(10, body.getQty());
      assertEquals(MovementType.IN, body.getType());

      // Verify
      verify(stockService).logMovement(anyString(), anyString(), any(StockDto.class), eq("1"));
    }
  }

  @Test
  @DisplayName("Log Movement Exception")
  void logMovement_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    when(stockService.logMovement(anyString(), anyString(), any(StockDto.class), anyString()))
        .thenThrow(RuntimeException.class);

    try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {

      mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn("1");

      // Act & Assert
      assertThrows(
          RuntimeException.class,
          () -> stockController.logMovement(stockDto, X_CORRELATION_ID, X_USER_EMAIL));

      // Verify
      verify(stockService).logMovement(anyString(), anyString(), any(StockDto.class), anyString());
    }
  }

  @Test
  @DisplayName("Get History Success")
  void getHistory_Success() throws Exception {
    // Arrange
    when(stockService.getHistory(anyString(), anyString())).thenReturn(List.of(stockDto));

    // Act
    ResponseEntity<List<StockDto>> response = stockController.getHistory(X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(1, response.getBody().size());

    // Verify
    verify(stockService).getHistory(anyString(), anyString());
  }

  @Test
  @DisplayName("Get History Exception")
  void getHistory_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    when(stockService.getHistory(anyString(), anyString())).thenThrow(RuntimeException.class);

    // Act & Assert
    assertThrows(RuntimeException.class, () -> stockController.getHistory(X_CORRELATION_ID));

    // Verify
    verify(stockService).getHistory(anyString(), anyString());
  }

  @Test
  @DisplayName("Get Low Stock Alerts Success")
  void getLowStockAlerts_Success() throws Exception {
    // Arrange
    ProductDto product = ProductDto.builder().id(1L).name("Laptop").qty(2).build();

    when(stockService.getLowStockAlerts(anyString(), anyString())).thenReturn(List.of(product));

    // Act
    ResponseEntity<List<ProductDto>> response = stockController.getLowStockAlerts(X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(1, response.getBody().size());

    // Verify
    verify(stockService).getLowStockAlerts(anyString(), anyString());
  }

  @Test
  @DisplayName("Get Low Stock Alerts Exception")
  void getLowStockAlerts_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    when(stockService.getLowStockAlerts(anyString(), anyString()))
        .thenThrow(RuntimeException.class);

    // Act & Assert
    assertThrows(RuntimeException.class, () -> stockController.getLowStockAlerts(X_CORRELATION_ID));

    // Verify
    verify(stockService).getLowStockAlerts(anyString(), anyString());
  }
}
