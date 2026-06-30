package com.stockiq.inventory.service;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.dto.ReportDto;
import java.util.List;

public interface ReportService {

  /**
   * Get Inventory Report.
   *
   * @param correlationId String
   * @param logKey String
   * @return List of InventoryReportItem
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  List<ReportDto.InventoryReportItem> getInventoryReport(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException;

  /**
   * Get Low Stock Report.
   *
   * @param correlationId String
   * @param logKey String
   * @return List of LowStockReportItem
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  List<ReportDto.LowStockReportItem> getLowStockReport(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException;

  /**
   * Get Supplier Performance Report.
   *
   * @param correlationId String
   * @param logKey String
   * @return List of SupplierPerformanceReportItem
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  List<ReportDto.SupplierPerformanceReportItem> getSupplierReport(
      String correlationId, String logKey) throws GeneralStatusCodeErrorException;

  /**
   * Export Inventory Report.
   *
   * @param correlationId String
   * @param logKey String
   * @param format String (PDF, CSV, Excel, etc.)
   * @return byte[] exported report file
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  byte[] exportInventoryReport(String correlationId, String logKey, String format)
      throws GeneralStatusCodeErrorException;

  /**
   * Export Low Stock Report.
   *
   * @param correlationId String
   * @param logKey String
   * @param format String (PDF, CSV, Excel, etc.)
   * @return byte[] exported report file
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  byte[] exportLowStockReport(String correlationId, String logKey, String format)
      throws GeneralStatusCodeErrorException;

  /**
   * Export Supplier Report.
   *
   * @param correlationId String
   * @param logKey String
   * @param format String (PDF, CSV, Excel, etc.)
   * @return byte[] exported report file
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  byte[] exportSupplierReport(String correlationId, String logKey, String format)
      throws GeneralStatusCodeErrorException;
}
