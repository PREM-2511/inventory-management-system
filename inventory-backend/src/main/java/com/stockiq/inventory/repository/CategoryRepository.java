package com.stockiq.inventory.repository;

import com.stockiq.inventory.common.enums.category.Status;
import com.stockiq.inventory.dto.CategoryDto;
import com.stockiq.inventory.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

  /**
   * Save Category.
   *
   * @param correlationId String
   * @param logKey String
   * @param category Category
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  long save(String correlationId, String logKey, Category category) throws Exception;

  /**
   * Update Category.
   *
   * @param correlationId String
   * @param logKey String
   * @param category Category
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  int update(String correlationId, String logKey, Category category) throws Exception;

  /**
   * Delete Category by ID.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  int delete(String correlationId, String logKey, long id) throws Exception;

  /**
   * Find Category by ID.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return Optional<CategoryDto>
   * @throws Exception if an error occurs during the operation
   */
  Optional<CategoryDto> findById(String correlationId, String logKey, long id) throws Exception;

  /**
   * Find All Categories with Pagination and Filters.
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
  List<Category> findAll(
      String correlationId, String logKey, int page, int size, String search, Status status)
      throws Exception;

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
  long countAll(String correlationId, String logKey, String search, Status status) throws Exception;

  /**
   * Find Category by Name.
   *
   * @param correlationId String
   * @param logKey String
   * @param name String
   * @return Optional<Category>
   * @throws Exception if an error occurs during the operation
   */
  Optional<Category> findByName(String correlationId, String logKey, String name) throws Exception;
}
