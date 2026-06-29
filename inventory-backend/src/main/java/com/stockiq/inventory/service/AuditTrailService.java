package com.stockiq.inventory.service;

import com.stockiq.inventory.common.enums.audit.AuditAction;
import com.stockiq.inventory.common.enums.audit.AuditEntityType;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.AuditTrailDto;

public interface AuditTrailService {

  /**
   * Record a new audit trail event.
   *
   * @param correlationId String
   * @param logKey String
   * @param action AuditAction
   * @param entityType AuditEntityType
   * @param entityId Long
   * @param entityName String
   * @param oldValue String
   * @param newValue String
   */
  void record(
      String correlationId,
      String logKey,
      AuditAction action,
      AuditEntityType entityType,
      Long entityId,
      String entityName,
      String oldValue,
      String newValue);

  /**
   * Get filtered and paginated audit trail.
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param entityType String
   * @param startDate String
   * @param endDate String
   * @return PageResponse<AuditTrailDto>
   * @throws Exception if an error occurs
   */
  PageResponse<AuditTrailDto> getAuditTrail(
      String correlationId,
      String logKey,
      int page,
      int size,
      String entityType,
      String startDate,
      String endDate)
      throws Exception;
}
