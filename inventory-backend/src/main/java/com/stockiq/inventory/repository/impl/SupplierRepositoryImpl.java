package com.stockiq.inventory.repository.impl;

import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.entity.Supplier;
import com.stockiq.inventory.repository.SupplierRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class SupplierRepositoryImpl implements SupplierRepository {

  private static final String REPO_IMPL = "supplier_repo_impl";

  private static final String INIT_LOG = "init_log";

  private static final String SELECT_ALL = "SELECT * FROM suppliers";

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public SupplierRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Save Supplier.
   *
   * @param correlationId String
   * @param logKey String
   * @param supplier Supplier
   * @return long
   */
  @Override
  public long save(String correlationId, String logKey, Supplier supplier) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);
    supplier.setUserId(com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong());

    String sql =
        """
        INSERT INTO suppliers
        (
            name,
            category,
            status,
            email,
            phone,
            created_by,
            created_at,
            user_id
        )
        VALUES
        (
            :name,
            :category,
            :status,
            :email,
            :phone,
            :createdBy,
            :createdAt,
            :userId
        )
        """;

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        sql, new BeanPropertySqlParameterSource(supplier), keyHolder, new String[] {"id"});

    return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0L;
  }

  /**
   * Update Supplier.
   *
   * @param correlationId String
   * @param logKey String
   * @param supplier Supplier
   * @return int
   */
  @Override
  public int update(String correlationId, String logKey, Supplier supplier) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        UPDATE suppliers
        SET name = :name,
            category = :category,
            status = :status,
            email = :email,
            phone = :phone,
            updated_by = :updatedBy,
            updated_at = :updatedAt
        WHERE id = :id AND user_id = :userId
        """;
    supplier.setUserId(com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong());

    return jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(supplier));
  }

  /**
   * Delete Supplier.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return int
   */
  @Override
  public int delete(String correlationId, String logKey, long id) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql = "DELETE FROM suppliers WHERE id = :id AND user_id = :userId";

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("id", id)
            .addValue(
                "userId", com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong());

    return jdbcTemplate.update(sql, params);
  }

  /**
   * Find Supplier by ID.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return Optional<Supplier>
   */
  @Override
  public Optional<Supplier> findById(String correlationId, String logKey, long id) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql = SELECT_ALL + " WHERE id = :id AND user_id = :userId";

    List<Supplier> suppliers =
        jdbcTemplate.query(
            sql,
            new MapSqlParameterSource("id", id)
                .addValue(
                    "userId",
                    com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong()),
            BeanPropertyRowMapper.newInstance(Supplier.class));

    return suppliers.stream().findFirst();
  }

  /**
   * Find All Suppliers with Pagination.
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param search String
   * @return List<Supplier>
   */
  @Override
  public List<Supplier> findAll(
      String correlationId, String logKey, int page, int size, String search) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    StringBuilder sql = new StringBuilder(SELECT_ALL + " WHERE user_id = :userId ");
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(
        "userId", com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong());

    if (search != null && !search.trim().isEmpty()) {
      sql.append(" AND (name LIKE :search OR category LIKE :search) ");
      params.addValue("search", "%" + search.trim() + "%");
    }

    sql.append(" ORDER BY name ASC LIMIT :limit OFFSET :offset ");
    params.addValue("limit", size);
    params.addValue("offset", Math.max(0, (page - 1) * size));

    return jdbcTemplate.query(
        sql.toString(), params, BeanPropertyRowMapper.newInstance(Supplier.class));
  }

  /**
   * Count All Suppliers.
   *
   * @param correlationId String
   * @param logKey String
   * @param search String
   * @return long
   */
  @Override
  public long countAll(String correlationId, String logKey, String search) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    StringBuilder sql =
        new StringBuilder("SELECT COUNT(*) FROM suppliers WHERE user_id = :userId ");
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(
        "userId", com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong());

    if (search != null && !search.trim().isEmpty()) {
      sql.append(" AND (name LIKE :search OR category LIKE :search) ");
      params.addValue("search", "%" + search.trim() + "%");
    }

    Long count = jdbcTemplate.queryForObject(sql.toString(), params, Long.class);

    return count != null ? count : 0L;
  }
}
