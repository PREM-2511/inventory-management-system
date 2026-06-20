package com.stockiq.inventory.repository.impl;

import com.stockiq.inventory.common.enums.category.Status;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.dto.CategoryDto;
import com.stockiq.inventory.entity.Category;
import com.stockiq.inventory.repository.CategoryRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

  private static final String REPO_IMPL = "category_repo_impl";

  private static final String INIT_LOG = "init_log";

  private static final String EXCEPTION_LOG = "exception_log";

  private static final String SELECT_ALL = "SELECT * FROM categories";

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public CategoryRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Save Category
   *
   * @param correlationId String
   * @param logKey String
   * @param category Category
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public long save(String correlationId, String logKey, Category category) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        INSERT INTO categories
        (
            name,
            icon,
            status,
            created_by,
            created_at
        )
        VALUES
        (
            :name,
            :icon,
            :status,
            :createdBy,
            :createdAt
        )
        """;

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        sql, new BeanPropertySqlParameterSource(category), keyHolder, new String[] {"id"});

    return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0L;
  }

  /**
   * Update Category
   *
   * @param correlationId String
   * @param logKey String
   * @param category Category
   * @return int
   */
  @Override
  public int update(String correlationId, String logKey, Category category) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        UPDATE categories
        SET name = :name,
            icon = :icon,
            status = :status,
            updated_by = :updatedBy,
            updated_at = :updatedAt
        WHERE id = :id
        """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("id", category.getId())
            .addValue("name", category.getName())
            .addValue("icon", category.getIcon())
            .addValue("status", category.getStatus())
            .addValue("updatedBy", category.getUpdatedBy())
            .addValue("updatedAt", category.getUpdatedAt());

    return jdbcTemplate.update(sql, params);
  }

  /**
   * Delete Category
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public int delete(String correlationId, String logKey, long id) {
    String sql = "DELETE FROM categories WHERE id = :id";

    return jdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
  }

  /**
   * Find Category By Id
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return Optional<CategoryDto>
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public Optional<CategoryDto> findById(String correlationId, String logKey, long id) {
    String sql = SELECT_ALL + " WHERE id = :id";

    List<Category> categories =
        jdbcTemplate.query(
            sql,
            new MapSqlParameterSource("id", id),
            BeanPropertyRowMapper.newInstance(Category.class));

    return categories.stream().findFirst().map(this::toDto);
  }

  /**
   * Find All Categories With Pagination
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param search String
   * @param status Status
   * @return List<Category>
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public List<Category> findAll(
      String correlationId, String logKey, int page, int size, String search, Status status) {
    StringBuilder sql = new StringBuilder(SELECT_ALL + " WHERE 1=1 ");
    MapSqlParameterSource params = new MapSqlParameterSource();

    if (search != null && !search.trim().isEmpty()) {
      sql.append(" AND name LIKE :search ");
      params.addValue("search", "%" + search.trim() + "%");
    }

    if (status != null) {
      sql.append(" AND status = :status ");
      params.addValue("status", status.getValue());
    }

    sql.append(" ORDER BY name ASC LIMIT :limit OFFSET :offset ");

    params.addValue("limit", size);
    params.addValue("offset", (page - 1) * size);

    return jdbcTemplate.query(
        sql.toString(), params, BeanPropertyRowMapper.newInstance(Category.class));
  }

  /**
   * Count all categories.
   *
   * @param correlationId String
   * @param logKey String
   * @param search String
   * @param status Status
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public long countAll(String correlationId, String logKey, String search, Status status) {
    StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM categories WHERE 1=1 ");
    MapSqlParameterSource params = new MapSqlParameterSource();

    if (search != null && !search.trim().isEmpty()) {
      sql.append(" AND name LIKE :search ");
      params.addValue("search", "%" + search.trim() + "%");
    }

    if (status != null) {
      sql.append(" AND status = :status ");
      params.addValue("status", status.getValue());
    }

    Long count = jdbcTemplate.queryForObject(sql.toString(), params, Long.class);

    return count != null ? count : 0L;
  }

  /**
   * Find Category by Name.
   *
   * @param correlationId String
   * @param logKey String
   * @param name String
   * @return Optional<Category>
   * @throws Exception if an error occurs during the operation
   */
  @Override
  public Optional<Category> findByName(String correlationId, String logKey, String name) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql = SELECT_ALL + " WHERE name = :name";

    List<Category> categories =
        jdbcTemplate.query(
            sql,
            new MapSqlParameterSource("name", name),
            BeanPropertyRowMapper.newInstance(Category.class));

    return categories.stream().findFirst();
  }

  private CategoryDto toDto(Category category) {
    return CategoryDto.builder()
        .id(category.getId())
        .name(category.getName())
        .icon(category.getIcon())
        .status(Status.fromValue(category.getStatus()))
        .createdBy(category.getCreatedBy())
        .createdAt(category.getCreatedAt() != null ? category.getCreatedAt().toString() : null)
        .updatedBy(category.getUpdatedBy())
        .updatedAt(category.getUpdatedAt() != null ? category.getUpdatedAt().toString() : null)
        .build();
  }
}
