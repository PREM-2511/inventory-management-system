package com.stockiq.inventory.controller;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.stock.LogMovementResponse;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.dto.StockDto;
import com.stockiq.inventory.service.StockService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
public class StockController {

  private static final String MAIN_LOG_KEY = "STOCK_";
  private static final String X_CORRELATION_ID = "X-Correlation-Id";
  private static final String X_USER_EMAIL = "X-User-Email";
  private static final String INIT_LOG = "init_log";
  private static final String CONTROLLER = "controller";

  private final StockService stockService;

  public StockController(StockService stockService) {
    this.stockService = stockService;
  }

  /**
   * Log a stock movement (IN or OUT).
   *
   * @param movementDto StockMovementDto
   * @param correlationId String
   * @param userEmail String - the logged-in user's email from request header
   * @return ResponseEntity<LogMovementResponse>
   */
  @PostMapping("/move")
  public ResponseEntity<LogMovementResponse> logMovement(
      @RequestBody @Valid StockDto movementDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId,
      @RequestHeader(value = X_USER_EMAIL, defaultValue = "system") String userEmail)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "LOG_MOVEMENT";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String finalUserEmail =
        (auth != null
                && auth.getPrincipal() != null
                && !auth.getPrincipal().toString().equals("anonymousUser"))
            ? auth.getPrincipal().toString()
            : userEmail;

    StockDto result = stockService.logMovement(correlationId, logKey, movementDto, finalUserEmail);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.CREATE.getValue()))
        .body(
            LogMovementResponse.builder()
                .id(result.getId())
                .type(result.getType())
                .product(result.getProduct())
                .qty(result.getQty())
                .date(result.getDate())
                .user(result.getUser())
                .build());
  }

  /**
   * Get full stock movement history.
   *
   * @param correlationId String
   * @return ResponseEntity<List<StockMovementDto>>
   */
  @GetMapping("/history")
  public ResponseEntity<List<StockDto>> getHistory(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "HISTORY";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(stockService.getHistory(correlationId, logKey));
  }

  /**
   * Get low stock and out-of-stock product alerts.
   *
   * @param correlationId String
   * @return ResponseEntity<List<ProductDto>>
   */
  @GetMapping("/alerts")
  public ResponseEntity<List<ProductDto>> getLowStockAlerts(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "ALERTS";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(stockService.getLowStockAlerts(correlationId, logKey));
  }
}
