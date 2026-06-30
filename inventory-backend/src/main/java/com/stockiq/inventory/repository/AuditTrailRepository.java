package com.stockiq.inventory.repository;

import com.stockiq.inventory.entity.AuditTrail;
import java.util.List;

public interface AuditTrailRepository {

  /**
   * Save Audit Trail record.
   *
   * @param correlationId String
   * @param logKey String
   * @param auditTrail AuditTrail
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  long save(String correlationId, String logKey, AuditTrail auditTrail) throws Exception;

  /**
   * Find all audit trail records with filters and pagination.
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param entityType String
   * @param startDate String
   * @param endDate String
   * @return List<AuditTrail>
   * @throws Exception if an error occurs during the operation
   */
  List<AuditTrail> findAll(
      String correlationId,
      String logKey,
      int page,
      int size,
      String entityType,
      String startDate,
      String endDate)
      throws Exception;

  /**
   * Count audit trail records with filters.
   *
   * @param correlationId String
   * @param logKey String
   * @param entityType String
   * @param startDate String
   * @param endDate String
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  long countAll(
      String correlationId, String logKey, String entityType, String startDate, String endDate)
      throws Exception;
}
