package com.stockiq.inventory.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryDto {
  private long totalProducts;
  private long totalSuppliers;
  private long lowStock;
  private long totalTransactions;
}
