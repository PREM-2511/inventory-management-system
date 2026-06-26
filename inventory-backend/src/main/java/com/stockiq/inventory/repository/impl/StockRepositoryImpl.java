package com.stockiq.inventory.repository.impl;

import com.stockiq.inventory.common.enums.stock.MovementType;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.dto.StockDto;
import com.stockiq.inventory.entity.Stock;
import com.stockiq.inventory.repository.StockRepository;
import java.util.List;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class StockRepositoryImpl implements StockRepository {

  private static final String REPO_IMPL = "stock_repo_impl";
  private static final String INIT_LOG = "init_log";

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public StockRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {

    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Save stock transaction.
   *
   * @param correlationId String
   * @param logKey String
   * @param transaction StockTransaction
   * @return long
   */
  @Override
  public long save(String correlationId, String logKey, Stock transaction) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);
    transaction.setUserId(com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong());

    String sql =
        """
        INSERT INTO stock_transactions
        (
            type,
            product_id,
            qty,
            created_by,
            created_at,
            user_id
        )
        VALUES
        (
            :type,
            :productId,
            :qty,
            :createdBy,
            :createdAt,
            :userId
        )
        """;

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        sql, new BeanPropertySqlParameterSource(transaction), keyHolder, new String[] {"id"});

    return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0L;
  }

  /**
   * Get all stock movement history.
   *
   * @param correlationId String
   * @param logKey String
   * @return List<StockMovementDto>
   */
  @Override
  public List<StockDto> findAll(String correlationId, String logKey) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        SELECT
            t.id,
            t.type,
            p.name AS product,
            t.qty,
            t.created_at AS date,
            COALESCE(u.email, t.created_by) AS user
        FROM stock_transactions t
        LEFT JOIN products p
            ON p.id = t.product_id
        LEFT JOIN users u
            ON t.user_id = u.id
        WHERE t.user_id = :userId
        ORDER BY t.created_at DESC
        """;

    return jdbcTemplate.query(
        sql,
        new MapSqlParameterSource(
            "userId", com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong()),
        (rs, rowNum) ->
            StockDto.builder()
                .id(rs.getLong("id"))
                .type(MovementType.fromValue(rs.getInt("type")))
                .product(rs.getString("product"))
                .qty(rs.getInt("qty"))
                .date(rs.getTimestamp("date") != null ? rs.getTimestamp("date").toString() : null)
                .user(rs.getString("user"))
                .build());
  }

  /**
   * Count all stock transactions.
   *
   * @param correlationId String
   * @param logKey String
   * @return long
   */
  @Override
  public long countAll(String correlationId, String logKey) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        SELECT COUNT(*)
        FROM stock_transactions
        WHERE user_id = :userId
        """;

    Long count =
        jdbcTemplate.queryForObject(
            sql,
            new MapSqlParameterSource(
                "userId", com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong()),
            Long.class);

    return count != null ? count : 0L;
  }
}
