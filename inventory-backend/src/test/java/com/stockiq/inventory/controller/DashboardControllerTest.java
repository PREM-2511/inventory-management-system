package com.stockiq.inventory.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.stockiq.inventory.common.enums.ErrorMessageCodeEnum;
import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.dto.DashboardCategoryDto;
import com.stockiq.inventory.dto.DashboardSummaryDto;
import com.stockiq.inventory.service.DashboardService;
import java.util.ArrayList;
import java.util.List;
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
@DisplayName("DashboardController Test")
class DashboardControllerTest {

  private static final String X_CORRELATION_ID = "X-Correlation-Id";

  @Mock private DashboardService dashboardService;

  @InjectMocks private DashboardController dashboardController;

  private DashboardSummaryDto dashboardSummaryDto;
  private List<DashboardCategoryDto> categoryList;

  @BeforeEach
  void setUp() {

    dashboardSummaryDto = new DashboardSummaryDto();
    dashboardSummaryDto.setTotalProducts(100L);
    dashboardSummaryDto.setTotalSuppliers(20L);
    dashboardSummaryDto.setLowStock(5L);
    dashboardSummaryDto.setTotalTransactions(250L);

    DashboardCategoryDto category = new DashboardCategoryDto();
    category.setName("Electronics");
    category.setCount(50L);
    category.setPercentage(40);

    categoryList = new ArrayList<>();
    categoryList.add(category);
  }

  @Test
  @DisplayName("Get Dashboard Summary Success")
  void getSummary_Success() throws Exception {
    // Arrange
    when(dashboardService.getSummary(anyString(), anyString())).thenReturn(dashboardSummaryDto);

    // Act
    ResponseEntity<DashboardSummaryDto> response = dashboardController.getSummary(X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(dashboardSummaryDto, response.getBody());

    // Verify
    verify(dashboardService).getSummary(anyString(), anyString());
  }

  @Test
  @DisplayName("Get Dashboard Summary Throws Exception")
  void getSummary_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Dashboard Summary Failed",
            "Summary Error",
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
            ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
            new java.util.Date().toString(),
            null);

    when(dashboardService.getSummary(anyString(), anyString()))
        .thenThrow(new GeneralStatusCodeErrorException(errorMessage));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> dashboardController.getSummary(X_CORRELATION_ID));

    // Verify
    verify(dashboardService).getSummary(anyString(), anyString());
  }

  @Test
  @DisplayName("Get Category Distribution Success")
  void getCategoryDistribution_Success() throws Exception {
    // Arrange
    when(dashboardService.getCategoryDistribution(anyString(), anyString()))
        .thenReturn(categoryList);

    // Act
    ResponseEntity<List<DashboardCategoryDto>> response =
        dashboardController.getCategoryDistribution(X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(categoryList, response.getBody());

    // Verify
    verify(dashboardService).getCategoryDistribution(anyString(), anyString());
  }

  @Test
  @DisplayName("Get Category Distribution Throws Exception")
  void getCategoryDistribution_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Category Distribution Failed",
            "Category Error",
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
            ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
            new java.util.Date().toString(),
            null);

    when(dashboardService.getCategoryDistribution(anyString(), anyString()))
        .thenThrow(new GeneralStatusCodeErrorException(errorMessage));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> dashboardController.getCategoryDistribution(X_CORRELATION_ID));

    // Verify
    verify(dashboardService).getCategoryDistribution(anyString(), anyString());
  }
}
