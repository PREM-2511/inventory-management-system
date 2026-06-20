package com.stockiq.inventory.service;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.dto.DashboardCategoryDto;
import com.stockiq.inventory.dto.DashboardSummaryDto;
import java.util.List;

public interface DashboardService {

  /**
   * Get Dashboard Summary
   *
   * @param correlationId String
   * @param logKey String
   * @return DashboardSummaryDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  DashboardSummaryDto getSummary(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException;

  /**
   * Get Category Distribution
   *
   * @param correlationId String
   * @param logKey String
   * @return List<DashboardCategoryDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  List<DashboardCategoryDto> getCategoryDistribution(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException;
}
