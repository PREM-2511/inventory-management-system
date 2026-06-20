package com.stockiq.inventory.service;

import com.stockiq.inventory.common.enums.category.Status;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.category.CategoryDetails;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.CategoryDto;
import com.stockiq.inventory.entity.Category;

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
  void update(
      String correlationId, String logKey, CategoryDto categoryDto, long id, String username);

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
   * Find all categories with pagination and filters.
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param search String
   * @param status Status
   * @return PageResponse<CategoryDetails>
   */
  PageResponse<CategoryDetails> findAll(
      String correlationId, String logKey, int page, int size, String search, Status status);

  /**
   * Find Category by Name.
   *
   * @param correlationId String
   * @param logKey String
   * @param name String
   * @return Category
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  Category findByName(String correlationId, String logKey, String name)
      throws GeneralStatusCodeErrorException;
}
