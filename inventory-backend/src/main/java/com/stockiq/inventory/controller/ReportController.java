package com.stockiq.inventory.controller;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.dto.ReportDto;
import com.stockiq.inventory.service.ReportService;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

  private static final String MAIN_LOG_KEY = "REPORT_";
  private static final String X_CORRELATION_ID = "X-Correlation-Id";
  private static final String INIT_LOG = "init_log";
  private static final String CONTROLLER = "controller";

  private final ReportService reportService;

  public ReportController(ReportService reportService) {
    this.reportService = reportService;
  }

  /**
   * Get Inventory Report.
   *
   * @param correlationId String
   * @return List of InventoryReportItem
   * @throws GeneralStatusCodeErrorException if an error occurs
   */
  @GetMapping("/inventory")
  public ResponseEntity<List<ReportDto.InventoryReportItem>> getInventoryReport(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "INVENTORY";
    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    List<ReportDto.InventoryReportItem> data =
        reportService.getInventoryReport(correlationId, logKey);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(data);
  }

  /**
   * Export Inventory Report.
   *
   * @param format String (pdf/excel)
   * @param correlationId String
   * @return ResponseEntity<byte[]>
   * @throws GeneralStatusCodeErrorException if an error occurs
   */
  @GetMapping("/inventory/export")
  public ResponseEntity<byte[]> exportInventoryReport(
      @RequestParam(defaultValue = "pdf") String format,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "INVENTORY_EXPORT";
    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    byte[] content = reportService.exportInventoryReport(correlationId, logKey, format);

    return buildExportResponse(content, "inventory_report", format);
  }

  /**
   * Get Low Stock Report.
   *
   * @param correlationId String
   * @return List of LowStockReportItem
   * @throws GeneralStatusCodeErrorException if an error occurs
   */
  @GetMapping("/low-stock")
  public ResponseEntity<List<ReportDto.LowStockReportItem>> getLowStockReport(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "LOW_STOCK";
    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    List<ReportDto.LowStockReportItem> data =
        reportService.getLowStockReport(correlationId, logKey);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(data);
  }

  /**
   * Export Low Stock Report.
   *
   * @param format String (pdf/excel)
   * @param correlationId String
   * @return ResponseEntity<byte[]>
   * @throws GeneralStatusCodeErrorException if an error occurs
   */
  @GetMapping("/low-stock/export")
  public ResponseEntity<byte[]> exportLowStockReport(
      @RequestParam(defaultValue = "pdf") String format,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "LOW_STOCK_EXPORT";
    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    byte[] content = reportService.exportLowStockReport(correlationId, logKey, format);

    return buildExportResponse(content, "low_stock_report", format);
  }

  /**
   * Get Supplier Performance Report.
   *
   * @param correlationId String
   * @return List of SupplierPerformanceReportItem
   * @throws GeneralStatusCodeErrorException if an error occurs
   */
  @GetMapping("/supplier")
  public ResponseEntity<List<ReportDto.SupplierPerformanceReportItem>> getSupplierReport(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "SUPPLIER";
    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    List<ReportDto.SupplierPerformanceReportItem> data =
        reportService.getSupplierReport(correlationId, logKey);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(data);
  }

  /**
   * Export Supplier Performance Report.
   *
   * @param format String (pdf/excel)
   * @param correlationId String
   * @return ResponseEntity<byte[]>
   * @throws GeneralStatusCodeErrorException if an error occurs
   */
  @GetMapping("/supplier/export")
  public ResponseEntity<byte[]> exportSupplierReport(
      @RequestParam(defaultValue = "pdf") String format,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "SUPPLIER_EXPORT";
    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    byte[] content = reportService.exportSupplierReport(correlationId, logKey, format);

    return buildExportResponse(content, "supplier_performance_report", format);
  }

  private ResponseEntity<byte[]> buildExportResponse(
      byte[] content, String filename, String format) {

    HttpHeaders headers = new HttpHeaders();

    if ("excel".equalsIgnoreCase(format)) {
      headers.setContentType(
          MediaType.parseMediaType(
              "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
      headers.set(
          HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + ".xlsx\"");
    } else {
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + ".pdf\"");
    }

    return new ResponseEntity<>(content, headers, HttpStatus.OK);
  }
}
