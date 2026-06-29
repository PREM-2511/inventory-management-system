package com.stockiq.inventory.controller;

import com.stockiq.inventory.common.enums.ErrorMessageCodeEnum;
import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.AuditTrailDto;
import com.stockiq.inventory.service.AuditTrailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit-trail")
public class AuditTrailController {

  private static final String MAIN_LOG_KEY = "AUDIT_";
  private static final String X_CORRELATION_ID = "X-Correlation-Id";
  private static final String INIT_LOG = "init_log";
  private static final String CONTROLLER = "controller";

  private final AuditTrailService auditTrailService;

  public AuditTrailController(AuditTrailService auditTrailService) {
    this.auditTrailService = auditTrailService;
  }

  /**
   * Find all audit trail records with pagination and filters.
   *
   * @param correlationId String
   * @param page int
   * @param size int
   * @param entityType String
   * @param startDate String
   * @param endDate String
   * @return ResponseEntity<PageResponse<AuditTrailDto>>
   * @throws GeneralStatusCodeErrorException if an error occurs
   */
  @GetMapping
  public ResponseEntity<PageResponse<AuditTrailDto>> list(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String entityType,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate)
      throws Exception {

    String logKey = MAIN_LOG_KEY + "FIND_ALL";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    PageResponse<AuditTrailDto> auditTrail =
        auditTrailService.getAuditTrail(
            correlationId, logKey, page, size, entityType, startDate, endDate);

    return ResponseEntity.ok(auditTrail);
  }
}
