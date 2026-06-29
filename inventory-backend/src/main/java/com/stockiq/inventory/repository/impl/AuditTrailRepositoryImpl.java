package com.stockiq.inventory.repository.impl;

import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.entity.AuditTrail;
import com.stockiq.inventory.repository.AuditTrailRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditTrailRepositoryImpl implements AuditTrailRepository {

  private static final String REPO_IMPL = "audit_trail_repo_impl";
  private static final String INIT_LOG = "init_log";

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public AuditTrailRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<AuditTrail> rowMapper =
      (rs, rowNum) ->
          AuditTrail.builder()
              .id(rs.getLong("id"))
              .userId(rs.getLong("user_id"))
              .userEmail(rs.getString("user_email"))
              .action(rs.getInt("action"))
              .entityType(rs.getInt("entity_type"))
              .entityId(rs.getObject("entity_id") != null ? rs.getLong("entity_id") : null)
              .entityName(rs.getString("entity_name"))
              .oldValue(rs.getString("old_value"))
              .newValue(rs.getString("new_value"))
              .createdAt(rs.getTimestamp("created_at"))
              .build();

  @Override
  public long save(String correlationId, String logKey, AuditTrail auditTrail) throws Exception {
    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        INSERT INTO audit_trail
        (
            user_id,
            user_email,
            action,
            entity_type,
            entity_id,
            entity_name,
            old_value,
            new_value,
            created_at
        )
        VALUES
        (
            :userId,
            :userEmail,
            :action,
            :entityType,
            :entityId,
            :entityName,
            :oldValue,
            :newValue,
            :createdAt
        )
        """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("userId", auditTrail.getUserId())
            .addValue("userEmail", auditTrail.getUserEmail())
            .addValue("action", auditTrail.getAction())
            .addValue("entityType", auditTrail.getEntityType())
            .addValue("entityId", auditTrail.getEntityId())
            .addValue("entityName", auditTrail.getEntityName())
            .addValue("oldValue", auditTrail.getOldValue())
            .addValue("newValue", auditTrail.getNewValue())
            .addValue("createdAt", auditTrail.getCreatedAt());

    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(sql, params, keyHolder, new String[] {"id"});

    return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0L;
  }

  @Override
  public List<AuditTrail> findAll(
      String correlationId,
      String logKey,
      int page,
      int size,
      String entityType,
      String startDate,
      String endDate)
      throws Exception {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    StringBuilder sql = new StringBuilder("SELECT * FROM audit_trail WHERE user_id = :userId ");
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("userId", SecurityUtils.getCurrentUserIdAsLong());

    if (entityType != null && !entityType.trim().isEmpty() && !entityType.equalsIgnoreCase("ALL")) {
      sql.append(" AND entity_type = :entityType ");
      params.addValue("entityType", entityType.trim().toUpperCase());
    }

    if (startDate != null && !startDate.trim().isEmpty()) {
      sql.append(" AND created_at >= :startDate ");
      params.addValue("startDate", startDate.trim() + " 00:00:00");
    }

    if (endDate != null && !endDate.trim().isEmpty()) {
      sql.append(" AND created_at <= :endDate ");
      params.addValue("endDate", endDate.trim() + " 23:59:59");
    }

    sql.append(" ORDER BY created_at DESC LIMIT :limit OFFSET :offset ");
    params.addValue("limit", size);
    params.addValue("offset", Math.max(0, (page - 1) * size));

    return jdbcTemplate.query(sql.toString(), params, rowMapper);
  }

  @Override
  public long countAll(
      String correlationId, String logKey, String entityType, String startDate, String endDate)
      throws Exception {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    StringBuilder sql =
        new StringBuilder("SELECT COUNT(id) FROM audit_trail WHERE user_id = :userId ");
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("userId", SecurityUtils.getCurrentUserIdAsLong());

    if (entityType != null && !entityType.trim().isEmpty() && !entityType.equalsIgnoreCase("ALL")) {
      sql.append(" AND entity_type = :entityType ");
      params.addValue("entityType", entityType.trim().toUpperCase());
    }

    if (startDate != null && !startDate.trim().isEmpty()) {
      sql.append(" AND created_at >= :startDate ");
      params.addValue("startDate", startDate.trim() + " 00:00:00");
    }

    if (endDate != null && !endDate.trim().isEmpty()) {
      sql.append(" AND created_at <= :endDate ");
      params.addValue("endDate", endDate.trim() + " 23:59:59");
    }

    Long count = jdbcTemplate.queryForObject(sql.toString(), params, Long.class);
    return count != null ? count : 0L;
  }
}
