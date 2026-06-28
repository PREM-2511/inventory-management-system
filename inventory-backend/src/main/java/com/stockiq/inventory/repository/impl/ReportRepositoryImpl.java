package com.stockiq.inventory.repository.impl;

import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.dto.ReportDto;
import com.stockiq.inventory.repository.ReportRepository;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepositoryImpl implements ReportRepository {

  private static final String REPO_IMPL = "report_repo_impl";
  private static final String INIT_LOG = "init_log";

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public ReportRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Get Inventory Report Items from database.
   *
   * @param correlationId String
   * @param logKey String
   * @param userId Long
   * @return List<ReportDto.InventoryReportItem>
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public List<ReportDto.InventoryReportItem> getInventoryReport(
      String correlationId, String logKey, Long userId) throws Exception {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        SELECT
            p.name AS productName,
            c.name AS categoryName,
            p.qty AS currentQty,
            (SELECT GROUP_CONCAT(s.name ORDER BY s.name SEPARATOR ', ')
             FROM suppliers s
             WHERE s.category = c.name AND s.user_id = p.user_id AND s.status = 'Active') AS supplierNames
        FROM products p
        LEFT JOIN categories c ON p.category_id = c.id
        WHERE p.user_id = :userId
        ORDER BY p.name ASC
        """;

    return jdbcTemplate.query(
        sql,
        Map.of("userId", userId),
        BeanPropertyRowMapper.newInstance(ReportDto.InventoryReportItem.class));
  }

  /**
   * Get Low Stock Report Items from database.
   *
   * @param correlationId String
   * @param logKey String
   * @param userId Long
   * @return List<ReportDto.LowStockReportItem>
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public List<ReportDto.LowStockReportItem> getLowStockReport(
      String correlationId, String logKey, Long userId) throws Exception {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        SELECT
            p.name AS productName,
            c.name AS categoryName,
            p.qty AS currentQty,
            p.reorder_point AS reorderThreshold,
            (SELECT GROUP_CONCAT(s.name ORDER BY s.name SEPARATOR ', ')
             FROM suppliers s
             WHERE s.category = c.name AND s.user_id = p.user_id AND s.status = 'Active') AS supplierNames
        FROM products p
        LEFT JOIN categories c ON p.category_id = c.id
        WHERE p.user_id = :userId AND p.qty <= p.reorder_point
        ORDER BY p.qty ASC
        """;

    return jdbcTemplate.query(
        sql,
        Map.of("userId", userId),
        BeanPropertyRowMapper.newInstance(ReportDto.LowStockReportItem.class));
  }

  /**
   * Get Supplier Report Items from database.
   *
   * @param correlationId String
   * @param logKey String
   * @param userId Long
   * @return List<ReportDto.SupplierPerformanceReportItem>
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public List<ReportDto.SupplierPerformanceReportItem> getSupplierReport(
      String correlationId, String logKey, Long userId) throws Exception {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        SELECT
            s.name AS supplierName,
            s.category AS categorySupplied,
            s.status AS status,
            s.email AS email,
            s.phone AS phone,
            COALESCE(COUNT(p.id), 0) AS totalProducts,
            COALESCE(SUM(p.qty), 0) AS totalStockQuantity
        FROM suppliers s
        LEFT JOIN categories c ON s.category = c.name AND s.user_id = c.user_id
        LEFT JOIN products p ON c.id = p.category_id AND c.user_id = p.user_id
        WHERE s.user_id = :userId
        GROUP BY s.id, s.name, s.category, s.status, s.email, s.phone
        ORDER BY s.name ASC
        """;

    return jdbcTemplate.query(
        sql,
        Map.of("userId", userId),
        BeanPropertyRowMapper.newInstance(ReportDto.SupplierPerformanceReportItem.class));
  }
}
