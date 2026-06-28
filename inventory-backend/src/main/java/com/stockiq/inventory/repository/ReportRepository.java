package com.stockiq.inventory.repository;

import com.stockiq.inventory.dto.ReportDto;
import java.util.List;

public interface ReportRepository {

  /**
   * Get Inventory Report Items from database.
   *
   * @param correlationId String
   * @param logKey String
   * @param userId Long
   * @return List<ReportDto.InventoryReportItem>
   * @throws Exception if an error occurs during the operation
   */
  List<ReportDto.InventoryReportItem> getInventoryReport(
      String correlationId, String logKey, Long userId) throws Exception;

  /**
   * Get Low Stock Report Items from database.
   *
   * @param correlationId String
   * @param logKey String
   * @param userId Long
   * @return List<ReportDto.LowStockReportItem>
   * @throws Exception if an error occurs during the operation
   */
  List<ReportDto.LowStockReportItem> getLowStockReport(
      String correlationId, String logKey, Long userId) throws Exception;

  /**
   * Get Supplier Report Items from database.
   *
   * @param correlationId String
   * @param logKey String
   * @param userId Long
   * @return List<ReportDto.SupplierPerformanceReportItem>
   * @throws Exception if an error occurs during the operation
   */
  List<ReportDto.SupplierPerformanceReportItem> getSupplierReport(
      String correlationId, String logKey, Long userId) throws Exception;
}
