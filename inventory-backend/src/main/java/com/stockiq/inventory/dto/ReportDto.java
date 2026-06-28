package com.stockiq.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReportDto {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class InventoryReportItem {
    private String productName;
    private String categoryName;
    private Integer currentQty;
    private String supplierNames;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class LowStockReportItem {
    private String productName;
    private String categoryName;
    private Integer currentQty;
    private Integer reorderThreshold;
    private String supplierNames;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SupplierPerformanceReportItem {
    private String supplierName;
    private String categorySupplied;
    private String status;
    private String email;
    private String phone;
    private Integer totalProducts;
    private Integer totalStockQuantity;
  }
}
