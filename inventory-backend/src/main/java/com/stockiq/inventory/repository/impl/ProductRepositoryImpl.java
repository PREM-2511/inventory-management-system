package com.stockiq.inventory.repository.impl;

import com.stockiq.inventory.common.enums.product.ProductStatus;
import com.stockiq.inventory.common.enums.stock.StockLevel;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.entity.Product;
import com.stockiq.inventory.repository.ProductRepository;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

  private static final String REPO_IMPL = "product_repo_impl";

  private static final String INIT_LOG = "init_log";

  private static final String SELECT_DTO =
      """
      SELECT p.id,
             p.user_id AS userId,
             p.name,
             c.name AS category,
             p.qty,
             p.reorder_point AS reorder,
             p.created_by AS createdBy,
             p.created_at AS createdAt,
             p.updated_by AS updatedBy,
             p.updated_at AS updatedAt
      FROM products p
      LEFT JOIN categories c ON p.category_id = c.id
      """;

  private final RowMapper<ProductDto> productRowMapper =
      (rs, rowNum) -> {
        int qty = rs.getInt("qty");
        int reorderPoint = rs.getInt("reorder");
        StockLevel stockLevel = StockLevel.fromQty(qty, reorderPoint);

        return ProductDto.builder()
            .id(rs.getLong("id"))
            .userId(readNullableLong(rs, "userId"))
            .name(rs.getString("name"))
            .category(rs.getString("category"))
            .qty(qty)
            .reorder(reorderPoint)
            .status(toProductStatus(stockLevel))
            .stockLevel(stockLevel)
            .createdBy(rs.getString("createdBy"))
            .createdAt(asString(rs.getTimestamp("createdAt")))
            .updatedBy(rs.getString("updatedBy"))
            .updatedAt(asString(rs.getTimestamp("updatedAt")))
            .build();
      };

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public ProductRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Save Product
   *
   * @param correlationId String
   * @param logKey String
   * @param product Product
   * @return long
   */
  @Override
  public long save(String correlationId, String logKey, Product product) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);
    product.setUserId(com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong());

    String sql =
        """
        INSERT INTO products
        (
            name,
            category_id,
            qty,
            reorder_point,
            created_by,
            created_at,
            user_id
        )
        VALUES
        (
            :name,
            :categoryId,
            :qty,
            :reorderPoint,
            :createdBy,
            :createdAt,
            :userId
        )
        """;

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        sql, new BeanPropertySqlParameterSource(product), keyHolder, new String[] {"id"});

    return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0L;
  }

  /**
   * Update Product
   *
   * @param correlationId String
   * @param logKey String
   * @param product Product
   * @return int
   */
  @Override
  public int update(String correlationId, String logKey, Product product) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        UPDATE products
        SET name = :name,
            category_id = :categoryId,
            qty = :qty,
            reorder_point = :reorderPoint,
            updated_by = :updatedBy,
            updated_at = :updatedAt
        WHERE id = :id AND user_id = :userId
        """;
    product.setUserId(com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong());

    return jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(product));
  }

  /**
   * Delete Product
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return int
   */
  @Override
  public int delete(String correlationId, String logKey, long id) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql = "DELETE FROM products WHERE id = :id AND user_id = :userId";

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("id", id)
            .addValue(
                "userId", com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong());

    return jdbcTemplate.update(sql, params);
  }

  /**
   * Find Product By Id
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return Optional<ProductDto>
   */
  @Override
  public Optional<ProductDto> findById(String correlationId, String logKey, long id) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql = SELECT_DTO + " WHERE p.id = :id AND p.user_id = :userId";

    List<ProductDto> list =
        jdbcTemplate.query(
            sql,
            new MapSqlParameterSource("id", id)
                .addValue(
                    "userId",
                    com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong()),
            productRowMapper);

    return list.stream().findFirst();
  }

  /**
   * Find Product By Name
   *
   * @param correlationId String
   * @param logKey String
   * @param name String
   * @return Optional<Product>
   */
  @Override
  public Optional<Product> findByName(String correlationId, String logKey, String name) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql = "SELECT * FROM products WHERE name = :name AND user_id = :userId";

    List<Product> products =
        jdbcTemplate.query(
            sql,
            new MapSqlParameterSource("name", name)
                .addValue(
                    "userId",
                    com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong()),
            BeanPropertyRowMapper.newInstance(Product.class));

    return products.stream().findFirst();
  }

  /**
   * Find All Products
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param search String
   * @param category String
   * @return List<ProductDto>
   */
  @Override
  public List<ProductDto> findAll(
      String correlationId, String logKey, int page, int size, String search, String category) {

    StringBuilder sql = new StringBuilder(SELECT_DTO + " WHERE p.user_id = :userId ");
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(
        "userId", com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong());

    if (search != null && !search.trim().isEmpty()) {
      sql.append(" AND p.name LIKE :search ");
      params.addValue("search", "%" + search.trim() + "%");
    }

    if (category != null && !category.trim().isEmpty() && !category.equals("All categories")) {
      sql.append(" AND c.name = :category ");
      params.addValue("category", category.trim());
    }

    sql.append(" ORDER BY p.name ASC LIMIT :limit OFFSET :offset ");
    params.addValue("limit", size);
    params.addValue("offset", Math.max(0, (page - 1) * size));

    return jdbcTemplate.query(sql.toString(), params, productRowMapper);
  }

  /**
   * Count All Products
   *
   * @param correlationId String
   * @param logKey String
   * @param search String
   * @param category String
   * @return long
   */
  @Override
  public long countAll(String correlationId, String logKey, String search, String category) {

    StringBuilder sql =
        new StringBuilder(
            "SELECT COUNT(*) FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.user_id = :userId ");

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(
        "userId", com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong());

    if (search != null && !search.trim().isEmpty()) {
      sql.append(" AND p.name LIKE :search ");
      params.addValue("search", "%" + search.trim() + "%");
    }

    if (category != null && !category.trim().isEmpty() && !category.equals("All categories")) {
      sql.append(" AND c.name = :category ");
      params.addValue("category", category.trim());
    }

    Long count = jdbcTemplate.queryForObject(sql.toString(), params, Long.class);

    return count != null ? count : 0L;
  }

  /**
   * Get Low Stock Alerts
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ProductDto>
   */
  @Override
  public List<ProductDto> getLowStockAlerts(String correlationId, String logKey) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        SELECT_DTO + " WHERE p.qty <= p.reorder_point AND p.user_id = :userId ORDER BY p.qty ASC";

    return jdbcTemplate.query(
        sql,
        new MapSqlParameterSource(
            "userId", com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserIdAsLong()),
        productRowMapper);
  }

  /**
   * Get all low stock products across all users (no SecurityContext — for background jobs only).
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ProductDto>
   */
  @Override
  public List<ProductDto> getAllLowStockProducts(String correlationId, String logKey) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql = SELECT_DTO + " WHERE p.qty <= p.reorder_point ORDER BY p.qty ASC";

    return jdbcTemplate.query(sql, new MapSqlParameterSource(), productRowMapper);
  }

  private ProductStatus toProductStatus(StockLevel stockLevel) {
    return switch (stockLevel) {
      case NORMAL -> ProductStatus.IN_STOCK;
      case LOW -> ProductStatus.LOW_STOCK;
      case CRITICAL -> ProductStatus.CRITICAL;
      case OUT_OF_STOCK -> ProductStatus.OUT_OF_STOCK;
    };
  }

  private Long readNullableLong(ResultSet rs, String column) throws java.sql.SQLException {
    Object value = rs.getObject(column);
    return value != null ? rs.getLong(column) : null;
  }

  private String asString(Timestamp timestamp) {
    return timestamp != null ? timestamp.toString() : null;
  }
}
