package com.stockiq.inventory.service.impl;

import com.stockiq.inventory.common.enums.audit.AuditAction;
import com.stockiq.inventory.common.enums.audit.AuditEntityType;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.common.util.SystemFormats;
import com.stockiq.inventory.dto.AuditTrailDto;
import com.stockiq.inventory.entity.AuditTrail;
import com.stockiq.inventory.repository.AuditTrailRepository;
import com.stockiq.inventory.service.AuditTrailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class AuditTrailServiceImpl implements AuditTrailService {

  private static final String SERVICE_IMPL = "audit_trail_service_impl";
  private static final String EXCEPTION_LOG = "exception_log";

  private final AuditTrailRepository auditTrailRepository;

  public AuditTrailServiceImpl(AuditTrailRepository auditTrailRepository) {
    this.auditTrailRepository = auditTrailRepository;
  }

  /**
   * Record a new audit trail event. Runs in a separate or nested transaction if needed, but since
   * it's fire-and-forget, we use PROPAGATION_REQUIRES_NEW or standard propagation but catch all
   * errors. Standard transactional context is fine, but we use try-catch to guarantee it never
   * disrupts caller operations.
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void record(
      String correlationId,
      String logKey,
      AuditAction action,
      AuditEntityType entityType,
      Long entityId,
      String entityName,
      String oldValue,
      String newValue) {

    try {
      Long userId = SecurityUtils.getCurrentUserIdAsLong();
      String userEmail = SecurityUtils.getCurrentUserEmail();

      // If user details are not found in the SecurityContext, default to system.
      if (userId == null) {
        userId = 0L;
      }
      if (userEmail == null) {
        userEmail = "System";
      }

      AuditTrail audit =
          AuditTrail.builder()
              .userId(userId)
              .userEmail(userEmail)
              .action(action.getValue())
              .entityType(entityType.getValue())
              .entityId(entityId)
              .entityName(entityName)
              .oldValue(oldValue)
              .newValue(newValue)
              .createdAt(Timestamp.valueOf(LocalDateTime.now()))
              .build();

      auditTrailRepository.save(correlationId, logKey, audit);

    } catch (Exception e) {
      ActivityLog.logError(
          correlationId,
          logKey,
          SERVICE_IMPL,
          EXCEPTION_LOG,
          "Failed to write audit trail: " + e.getMessage());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<AuditTrailDto> getAuditTrail(
      String correlationId,
      String logKey,
      int page,
      int size,
      String entityType,
      String startDate,
      String endDate)
      throws Exception {

    try {
      List<AuditTrailDto> auditTrailList =
          auditTrailRepository
              .findAll(correlationId, logKey, page, size, entityType, startDate, endDate)
              .stream()
              .map(this::toDto)
              .toList();

      long total =
          auditTrailRepository.countAll(correlationId, logKey, entityType, startDate, endDate);

      return new PageResponse<>(auditTrailList, page, size, total);

    } catch (Exception e) {
      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
      throw e;
    }
  }

  private AuditTrailDto toDto(AuditTrail entity) {
    String formattedDate = "";
    if (entity.getCreatedAt() != null) {
      formattedDate =
          entity
              .getCreatedAt()
              .toLocalDateTime()
              .format(DateTimeFormatter.ofPattern(SystemFormats.DATE_TIME_FORMAT_1));
    }

    return AuditTrailDto.builder()
        .id(entity.getId())
        .userEmail(entity.getUserEmail())
        .action(AuditAction.fromValue(entity.getAction()).name())
        .entityType(AuditEntityType.fromValue(entity.getEntityType()).name())
        .entityId(entity.getEntityId())
        .entityName(entity.getEntityName())
        .oldValue(entity.getOldValue())
        .newValue(entity.getNewValue())
        .createdAt(formattedDate)
        .build();
  }
}
