package com.stockiq.inventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.dto.ReportDto;
import com.stockiq.inventory.repository.ReportRepository;
import com.stockiq.inventory.service.impl.ReportServiceImpl;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService Unit Test")
class ReportServiceTest {

  private static final String X_CORRELATION_ID = "X-Correlation-Id";

  private static final String LOG_KEY = "log-key";

  @Mock private ReportRepository reportRepository;

  @InjectMocks private ReportServiceImpl reportService;

  private MockedStatic<SecurityUtils> mockedSecurityUtils;

  @BeforeEach
  void setUp() {
    mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
    mockedSecurityUtils.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);
  }

  @AfterEach
  void tearDown() {
    mockedSecurityUtils.close();
  }

  @Test
  @DisplayName("getInventoryReport - successfully fetches inventory items")
  void getInventoryReport_success() throws Exception {
    // Arrange
    ReportDto.InventoryReportItem item =
        ReportDto.InventoryReportItem.builder()
            .productName("Product A")
            .categoryName("Category A")
            .currentQty(10)
            .supplierNames("Supplier A")
            .build();
    // Act & Assert
    when(reportRepository.getInventoryReport(anyString(), anyString(), anyLong()))
        .thenReturn(List.of(item));

    List<ReportDto.InventoryReportItem> result =
        reportService.getInventoryReport(X_CORRELATION_ID, LOG_KEY);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Product A", result.get(0).getProductName());
  }

  @Test
  @DisplayName("Get Inventory Report Exception")
  void getInventoryReport_Exception() throws Exception {
    // Arrange
    when(reportRepository.getInventoryReport(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("Database Error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> reportService.getInventoryReport(X_CORRELATION_ID, LOG_KEY));

    // Verify
    verify(reportRepository).getInventoryReport(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("getLowStockReport - successfully fetches low stock items")
  void getLowStockReport_success() throws Exception {
    // Arrange
    ReportDto.LowStockReportItem item =
        ReportDto.LowStockReportItem.builder()
            .productName("Product B")
            .categoryName("Category B")
            .currentQty(2)
            .reorderThreshold(5)
            .supplierNames("Supplier B")
            .build();

    // Act & Assert
    when(reportRepository.getLowStockReport(anyString(), anyString(), anyLong()))
        .thenReturn(List.of(item));

    List<ReportDto.LowStockReportItem> result =
        reportService.getLowStockReport(X_CORRELATION_ID, LOG_KEY);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Product B", result.get(0).getProductName());
  }

  @Test
  @DisplayName("Get Low Stock Report Exception")
  void getLowStockReport_Exception() throws Exception {
    // Arrange
    when(reportRepository.getLowStockReport(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("Database Error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> reportService.getLowStockReport(X_CORRELATION_ID, LOG_KEY));

    // Verify
    verify(reportRepository).getLowStockReport(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("getSupplierReport - successfully fetches supplier items")
  void getSupplierReport_success() throws Exception {
    ReportDto.SupplierPerformanceReportItem item =
        ReportDto.SupplierPerformanceReportItem.builder()
            .supplierName("Supplier C")
            .categorySupplied("Category C")
            .status("Active")
            .email("supplier@test.com")
            .phone("1234567")
            .totalProducts(5)
            .totalStockQuantity(50)
            .build();

    when(reportRepository.getSupplierReport(anyString(), anyString(), anyLong()))
        .thenReturn(List.of(item));

    List<ReportDto.SupplierPerformanceReportItem> result =
        reportService.getSupplierReport(X_CORRELATION_ID, LOG_KEY);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Supplier C", result.get(0).getSupplierName());
  }

  @Test
  @DisplayName("Get Supplier Report Exception")
  void getSupplierReport_Exception() throws Exception {
    // Arrange
    when(reportRepository.getSupplierReport(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("Database Error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> reportService.getSupplierReport(X_CORRELATION_ID, LOG_KEY));

    // Verify
    verify(reportRepository).getSupplierReport(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("exportInventoryReport - successfully exports to PDF and Excel")
  void exportInventoryReport_success() throws Exception {
    ReportDto.InventoryReportItem item =
        ReportDto.InventoryReportItem.builder()
            .productName("Product A")
            .categoryName("Category A")
            .currentQty(10)
            .supplierNames("Supplier A")
            .build();

    when(reportRepository.getInventoryReport(anyString(), anyString(), anyLong()))
        .thenReturn(List.of(item));

    byte[] pdfBytes = reportService.exportInventoryReport(X_CORRELATION_ID, LOG_KEY, "pdf");
    byte[] excelBytes = reportService.exportInventoryReport(X_CORRELATION_ID, LOG_KEY, "excel");

    assertNotNull(pdfBytes);
    assertTrue(pdfBytes.length > 0);
    assertNotNull(excelBytes);
    assertTrue(excelBytes.length > 0);
  }

  @Test
  @DisplayName("exportLowStockReport - successfully exports to PDF and Excel")
  void exportLowStockReport_success() throws Exception {
    ReportDto.LowStockReportItem item =
        ReportDto.LowStockReportItem.builder()
            .productName("Product B")
            .categoryName("Category B")
            .currentQty(2)
            .reorderThreshold(5)
            .supplierNames("Supplier B")
            .build();

    when(reportRepository.getLowStockReport(anyString(), anyString(), anyLong()))
        .thenReturn(List.of(item));

    byte[] pdfBytes = reportService.exportLowStockReport(X_CORRELATION_ID, LOG_KEY, "pdf");
    byte[] excelBytes = reportService.exportLowStockReport(X_CORRELATION_ID, LOG_KEY, "excel");

    assertNotNull(pdfBytes);
    assertTrue(pdfBytes.length > 0);
    assertNotNull(excelBytes);
    assertTrue(excelBytes.length > 0);
  }

  @Test
  @DisplayName("exportSupplierReport - successfully exports to PDF and Excel")
  void exportSupplierReport_success() throws Exception {
    ReportDto.SupplierPerformanceReportItem item =
        ReportDto.SupplierPerformanceReportItem.builder()
            .supplierName("Supplier C")
            .categorySupplied("Category C")
            .status("Active")
            .email("supplier@test.com")
            .phone("1234567")
            .totalProducts(5)
            .totalStockQuantity(50)
            .build();

    when(reportRepository.getSupplierReport(anyString(), anyString(), anyLong()))
        .thenReturn(List.of(item));

    byte[] pdfBytes = reportService.exportSupplierReport(X_CORRELATION_ID, LOG_KEY, "pdf");
    byte[] excelBytes = reportService.exportSupplierReport(X_CORRELATION_ID, LOG_KEY, "excel");

    assertNotNull(pdfBytes);
    assertTrue(pdfBytes.length > 0);
    assertNotNull(excelBytes);
    assertTrue(excelBytes.length > 0);
  }

  @Test
  @DisplayName("Export Inventory Report Invalid Format")
  void exportInventoryReport_InvalidFormat() throws Exception {
    // Arrange
    ReportDto.InventoryReportItem item =
        ReportDto.InventoryReportItem.builder()
            .productName("Laptop")
            .categoryName("Electronics")
            .currentQty(10)
            .supplierNames("ABC")
            .build();

    when(reportRepository.getInventoryReport(anyString(), anyString(), anyLong()))
        .thenReturn(List.of(item));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> reportService.exportInventoryReport(X_CORRELATION_ID, LOG_KEY, "csv"));

    // Verify
    verify(reportRepository).getInventoryReport(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Export Low Stock Report Invalid Format")
  void exportLowStockReport_InvalidFormat() throws Exception {
    // Arrange
    ReportDto.LowStockReportItem item =
        ReportDto.LowStockReportItem.builder()
            .productName("Laptop")
            .categoryName("Electronics")
            .currentQty(2)
            .reorderThreshold(5)
            .supplierNames("ABC Supplier")
            .build();

    when(reportRepository.getLowStockReport(anyString(), anyString(), anyLong()))
        .thenReturn(List.of(item));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> reportService.exportLowStockReport(X_CORRELATION_ID, LOG_KEY, "csv"));

    // Verify
    verify(reportRepository).getLowStockReport(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Export Supplier Report PDF Success")
  void exportSupplierReport_PdfSuccess() throws Exception {

    // Arrange
    ReportDto.SupplierPerformanceReportItem item =
        ReportDto.SupplierPerformanceReportItem.builder()
            .supplierName("Supplier A")
            .categorySupplied("Electronics")
            .status("ACTIVE")
            .totalProducts(10)
            .totalStockQuantity(100)
            .build();

    when(reportRepository.getSupplierReport(anyString(), anyString(), anyLong()))
        .thenReturn(List.of(item));

    // Act
    byte[] result = reportService.exportSupplierReport("corrId", "logKey", "pdf");

    // Assert
    assertNotNull(result);
    assertTrue(result.length > 0);

    // Verify
    verify(reportRepository).getSupplierReport(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Export Supplier Report Excel Success")
  void exportSupplierReport_ExcelSuccess() throws Exception {

    // Arrange
    ReportDto.SupplierPerformanceReportItem item =
        ReportDto.SupplierPerformanceReportItem.builder()
            .supplierName("Supplier A")
            .categorySupplied("Electronics")
            .status("ACTIVE")
            .totalProducts(10)
            .totalStockQuantity(100)
            .build();

    when(reportRepository.getSupplierReport(anyString(), anyString(), anyLong()))
        .thenReturn(List.of(item));

    // Act
    byte[] result = reportService.exportSupplierReport("corrId", "logKey", "excel");

    // Assert
    assertNotNull(result);
    assertTrue(result.length > 0);

    // Verify
    verify(reportRepository).getSupplierReport(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Export Supplier Report Exception Flow")
  void exportSupplierReport_Exception() throws Exception {

    // Arrange
    when(reportRepository.getSupplierReport(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("Database error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> reportService.exportSupplierReport("corrId", "logKey", "pdf"));

    // Verify
    verify(reportRepository).getSupplierReport(anyString(), anyString(), eq(1L));
  }
}
