package com.stockiq.inventory.service;

import com.stockiq.inventory.common.enums.category.Status;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.CategoryDto;

public interface CategoryService {

  /**
   * Save Category.
   *
   * @param correlationId String
   * @param logKey String
   * @param categoryDto CategoryDto
   * @return CategoryDto
   */
  CategoryDto save(String correlationId, String logKey, CategoryDto categoryDto);

  /**
   * Update Category.
   *
   * @param correlationId String
   * @param logKey String
   * @param categoryDto CategoryDto
   * @param id long
   */
  void update(String correlationId, String logKey, CategoryDto categoryDto, long id);

  /**
   * Delete Category by ID.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   */
  void delete(String correlationId, String logKey, long id);

  /**
   * Find Category by ID.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return CategoryDto
   */
  CategoryDto findById(String correlationId, String logKey, long id);

  /**
   * Find All Categories with Pagination and Filters.
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param search String
   * @param status Status
   * @return PageResponse<CategoryDto>
   */
  PageResponse<CategoryDto> findAll(
      String correlationId, String logKey, int page, int size, String search, Status status);
}
