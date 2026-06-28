package com.stockiq.inventory.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.dto.ReportDto;
import com.stockiq.inventory.service.ReportService;
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
@DisplayName("ReportController Test")
class ReportControllerTest {

  private static final String CORRELATION_ID = "X-Correlation-Id";

  @Mock private ReportService reportService;

  @InjectMocks private ReportController reportController;

  private List<ReportDto.InventoryReportItem> inventoryList;
  private List<ReportDto.LowStockReportItem> lowStockList;
  private List<ReportDto.SupplierPerformanceReportItem> supplierList;

  @BeforeEach
  void setUp() {

    ReportDto.InventoryReportItem inv = new ReportDto.InventoryReportItem();
    ReportDto.LowStockReportItem low = new ReportDto.LowStockReportItem();
    ReportDto.SupplierPerformanceReportItem sup = new ReportDto.SupplierPerformanceReportItem();

    inventoryList = List.of(inv);
    lowStockList = List.of(low);
    supplierList = List.of(sup);
  }

  @Test
  @DisplayName("Get Inventory Report Success")
  void getInventoryReport_Success() throws Exception {
    // Arrange
    when(reportService.getInventoryReport(anyString(), anyString())).thenReturn(inventoryList);

    // Act
    ResponseEntity<List<ReportDto.InventoryReportItem>> response =
        reportController.getInventoryReport(CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(inventoryList, response.getBody());

    // Verify
    verify(reportService).getInventoryReport(anyString(), anyString());
  }

  @Test
  @DisplayName("Export Inventory Report Success")
  void exportInventoryReport_Success() throws Exception {
    // Arrange
    byte[] mockBytes = "pdf-data".getBytes();

    when(reportService.exportInventoryReport(anyString(), anyString(), anyString()))
        .thenReturn(mockBytes);

    // Act
    ResponseEntity<byte[]> response = reportController.exportInventoryReport("pdf", CORRELATION_ID);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    // Verify
    verify(reportService).exportInventoryReport(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("Get Low Stock Report Success")
  void getLowStockReport_Success() throws Exception {
    // Arrange
    when(reportService.getLowStockReport(anyString(), anyString())).thenReturn(lowStockList);

    // Act
    ResponseEntity<List<ReportDto.LowStockReportItem>> response =
        reportController.getLowStockReport(CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(lowStockList, response.getBody());

    // Verify
    verify(reportService).getLowStockReport(anyString(), anyString());
  }

  @Test
  @DisplayName("Export Low Stock Report Success")
  void exportLowStockReport_Success() throws Exception {
    // Arrange
    byte[] mockBytes = "excel-data".getBytes();

    when(reportService.exportLowStockReport(anyString(), anyString(), anyString()))
        .thenReturn(mockBytes);

    // Act
    ResponseEntity<byte[]> response =
        reportController.exportLowStockReport("excel", CORRELATION_ID);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    // Verify
    verify(reportService).exportLowStockReport(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("Get Supplier Report Success")
  void getSupplierReport_Success() throws Exception {
    // Arrange
    when(reportService.getSupplierReport(anyString(), anyString())).thenReturn(supplierList);

    // Act
    ResponseEntity<List<ReportDto.SupplierPerformanceReportItem>> response =
        reportController.getSupplierReport(CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(supplierList, response.getBody());

    // Verify
    verify(reportService).getSupplierReport(anyString(), anyString());
  }

  @Test
  @DisplayName("Export Supplier Report Success")
  void exportSupplierReport_Success() throws Exception {
    // Arrange
    byte[] mockBytes = "supplier-data".getBytes();

    when(reportService.exportSupplierReport(anyString(), anyString(), anyString()))
        .thenReturn(mockBytes);

    // Act
    ResponseEntity<byte[]> response = reportController.exportSupplierReport("pdf", CORRELATION_ID);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    // Verify
    verify(reportService).exportSupplierReport(anyString(), anyString(), anyString());
  }
}
