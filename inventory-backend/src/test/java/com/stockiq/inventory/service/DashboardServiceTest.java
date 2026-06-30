package com.stockiq.inventory.service;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.dto.DashboardCategoryDto;
import com.stockiq.inventory.dto.DashboardSummaryDto;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.repository.StockRepository;
import com.stockiq.inventory.repository.SupplierRepository;
import com.stockiq.inventory.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

  private static final String CORRELATION_ID = "corr-id";
  private static final String LOG_KEY = "log-key";

  @Mock private ProductService productService;

  @Mock private SupplierRepository supplierRepository;

  @Mock private StockRepository stockRepository;

  @InjectMocks private DashboardServiceImpl dashboardService;

  private ProductDto product1;
  private ProductDto product2;
  private ProductDto product3;

  @BeforeEach
  void setUp() {

    product1 = new ProductDto();
    product1.setCategory("Electronics");
    product1.setQty(100);

    product2 = new ProductDto();
    product2.setCategory("Electronics");
    product2.setQty(50);

    product3 = new ProductDto();
    product3.setCategory("Furniture");
    product3.setQty(30);
  }

  @Test
  @DisplayName("get summary success")
  void getSummary_Success() throws Exception {
    // Arrange
    when(productService.getAllProducts(anyString(), anyString()))
        .thenReturn(List.of(product1, product2, product3));

    when(productService.getLowStockAlerts(anyString(), anyString())).thenReturn(List.of(product1));

    when(supplierRepository.countAll(anyString(), anyString(), anyString())).thenReturn(5L);

    when(stockRepository.countAll(anyString(), anyString())).thenReturn(10L);

    // Act
    DashboardSummaryDto result = dashboardService.getSummary(CORRELATION_ID, LOG_KEY);

    // Assert
    assertNotNull(result);
    assertEquals(3, result.getTotalProducts());
    assertEquals(5, result.getTotalSuppliers());
    assertEquals(10, result.getTotalTransactions());
    assertEquals(1, result.getLowStock());

    // Verify
    verify(productService, times(1)).getAllProducts(anyString(), anyString());

    verify(productService, times(1)).getLowStockAlerts(anyString(), anyString());

    verify(supplierRepository, times(1)).countAll(anyString(), anyString(), anyString());

    verify(stockRepository, times(1)).countAll(anyString(), anyString());
  }

  @Test
  @DisplayName("get summary exception")
  void getSummary_Exception() throws Exception {
    // Arrange
    when(productService.getAllProducts(anyString(), anyString()))
        .thenThrow(new RuntimeException("Database Error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> dashboardService.getSummary(CORRELATION_ID, LOG_KEY));

    // Verify
    verify(productService, times(1)).getAllProducts(anyString(), anyString());
  }

  @Test
  @DisplayName("get category distribution success")
  void getCategoryDistribution_Success() throws Exception {
    // Arrange
    when(productService.getAllProducts(anyString(), anyString()))
        .thenReturn(List.of(product1, product2, product3));

    // Act
    List<DashboardCategoryDto> result =
        dashboardService.getCategoryDistribution(CORRELATION_ID, LOG_KEY);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());

    DashboardCategoryDto electronics =
        result.stream().filter(c -> c.getName().equals("Electronics")).findFirst().orElse(null);

    assertNotNull(electronics);
    assertEquals(2L, electronics.getCount());
    assertEquals(67, electronics.getPercentage());
    assertEquals(150L, electronics.getTotalStock());

    DashboardCategoryDto furniture =
        result.stream().filter(c -> c.getName().equals("Furniture")).findFirst().orElse(null);

    assertNotNull(furniture);
    assertEquals(1L, furniture.getCount());
    assertEquals(33, furniture.getPercentage());
    assertEquals(30L, furniture.getTotalStock());

    // Verify
    verify(productService, times(1)).getAllProducts(anyString(), anyString());
  }

  @Test
  @DisplayName("get category distribution with unknown category")
  void getCategoryDistribution_UnknownCategory() throws Exception {
    // Arrange
    ProductDto unknown = new ProductDto();
    unknown.setCategory(null);
    unknown.setQty(10);

    when(productService.getAllProducts(anyString(), anyString())).thenReturn(List.of(unknown));

    // Act
    List<DashboardCategoryDto> result =
        dashboardService.getCategoryDistribution(CORRELATION_ID, LOG_KEY);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Unknown", result.get(0).getName());
    assertEquals(1L, result.get(0).getCount());
    assertEquals(100, result.get(0).getPercentage());
    assertEquals(10L, result.get(0).getTotalStock());

    // Verify
    verify(productService, times(1)).getAllProducts(anyString(), anyString());
  }

  @Test
  @DisplayName("get category distribution empty list")
  void getCategoryDistribution_EmptyList() throws Exception {
    // Arrange
    when(productService.getAllProducts(anyString(), anyString())).thenReturn(List.of());

    // Act
    List<DashboardCategoryDto> result =
        dashboardService.getCategoryDistribution(CORRELATION_ID, LOG_KEY);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());

    // Verify
    verify(productService, times(1)).getAllProducts(anyString(), anyString());
  }

  @Test
  @DisplayName("get category distribution exception")
  void getCategoryDistribution_Exception() throws Exception {
    // Arrange
    when(productService.getAllProducts(anyString(), anyString()))
        .thenThrow(new RuntimeException("Database Error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> dashboardService.getCategoryDistribution(CORRELATION_ID, LOG_KEY));

    // Verify
    verify(productService, times(1)).getAllProducts(anyString(), anyString());
  }
}
