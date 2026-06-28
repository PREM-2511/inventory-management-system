package com.stockiq.inventory.repo;

import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.dto.ReportDto;
import com.stockiq.inventory.repository.impl.ReportRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Report Repository Test")
@ExtendWith(MockitoExtension.class)
class ReportRepositoryTest {

  private final String X_CORRELATION_ID = "X-Correlation-Id";
  private final String LOG_KEY = "test-log-key";
  private final Long USER_ID = 10L;

  @Mock private NamedParameterJdbcTemplate jdbcTemplate;

  @InjectMocks private ReportRepositoryImpl reportRepository;

  private ReportDto.InventoryReportItem inventoryItem;
  private ReportDto.LowStockReportItem lowStockItem;
  private ReportDto.SupplierPerformanceReportItem supplierItem;

  @BeforeEach
  void setUp() {

    inventoryItem = new ReportDto.InventoryReportItem();
    inventoryItem.setProductName("Laptop");

    lowStockItem = new ReportDto.LowStockReportItem();
    lowStockItem.setProductName("Mouse");

    supplierItem = new ReportDto.SupplierPerformanceReportItem();
    supplierItem.setSupplierName("ABC Supplier");
  }

  @Test
  @DisplayName("getInventoryReport returns list successfully")
  void getInventoryReport_Success() throws Exception {

    //  Arrange
    when(jdbcTemplate.query(anyString(), any(Map.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(inventoryItem));

    try (MockedStatic<ActivityLog> mocked = mockStatic(ActivityLog.class)) {

      mocked
          .when(
              () -> ActivityLog.logInfo(X_CORRELATION_ID, LOG_KEY, "report_repo_impl", "init_log"))
          .thenAnswer(inv -> null);

      // Act
      List<ReportDto.InventoryReportItem> result =
          reportRepository.getInventoryReport(X_CORRELATION_ID, LOG_KEY, USER_ID);

      //  Assert
      assertEquals(1, result.size());
      assertEquals("Laptop", result.get(0).getProductName());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(Map.class), any(BeanPropertyRowMapper.class));
    }
  }

  @Test
  @DisplayName("getLowStockReport returns list successfully")
  void getLowStockReport_Success() throws Exception {
    //  Arrange
    when(jdbcTemplate.query(anyString(), any(Map.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(lowStockItem));

    try (MockedStatic<ActivityLog> mocked = mockStatic(ActivityLog.class)) {

      mocked
          .when(
              () -> ActivityLog.logInfo(X_CORRELATION_ID, LOG_KEY, "report_repo_impl", "init_log"))
          .thenAnswer(inv -> null);

      // Act
      List<ReportDto.LowStockReportItem> result =
          reportRepository.getLowStockReport(X_CORRELATION_ID, LOG_KEY, USER_ID);

      // Assert
      assertEquals(1, result.size());
      assertEquals("Mouse", result.get(0).getProductName());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(Map.class), any(BeanPropertyRowMapper.class));
    }
  }

  @Test
  @DisplayName("getSupplierReport returns list successfully")
  void getSupplierReport_Success() throws Exception {
    //  Arrange
    when(jdbcTemplate.query(anyString(), any(Map.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(supplierItem));

    try (MockedStatic<ActivityLog> mocked = mockStatic(ActivityLog.class)) {

      mocked
          .when(
              () -> ActivityLog.logInfo(X_CORRELATION_ID, LOG_KEY, "report_repo_impl", "init_log"))
          .thenAnswer(inv -> null);

      // Act
      List<ReportDto.SupplierPerformanceReportItem> result =
          reportRepository.getSupplierReport(X_CORRELATION_ID, LOG_KEY, USER_ID);

      // Assert
      assertEquals(1, result.size());
      assertEquals("ABC Supplier", result.get(0).getSupplierName());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(Map.class), any(BeanPropertyRowMapper.class));
    }
  }
}
