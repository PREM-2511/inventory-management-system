package com.stockiq.inventory.controller;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.dto.DashboardCategoryDto;
import com.stockiq.inventory.dto.DashboardSummaryDto;
import com.stockiq.inventory.service.DashboardService;
import java.util.List;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

  private static final String MAIN_LOG_KEY = "DASHBOARD_";
  private static final String X_CORRELATION_ID = "X-Correlation-Id";
  private static final String INIT_LOG = "init_log";
  private static final String CONTROLLER = "controller";

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  /**
   * Get dashboard summary: totals for products, suppliers, low-stock, and transactions.
   *
   * @param correlationId String
   * @return ResponseEntity<DashboardSummaryDto>
   */
  @GetMapping("/summary")
  public ResponseEntity<DashboardSummaryDto> getSummary(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "SUMMARY";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(dashboardService.getSummary(correlationId, logKey));
  }

  /**
   * Get product distribution by category.
   *
   * @param correlationId String
   * @return ResponseEntity<List<DashboardCategoryDto>>
   */
  @GetMapping("/categories")
  public ResponseEntity<List<DashboardCategoryDto>> getCategoryDistribution(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "CATEGORIES";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(dashboardService.getCategoryDistribution(correlationId, logKey));
  }
}
