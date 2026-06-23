package com.stockiq.inventory.service;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.ProductDto;
import java.util.List;

public interface ProductService {

  /**
   * Save Product
   *
   * @param correlationId String
   * @param logKey String
   * @param productDto ProductDto
   * @return ProductDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  ProductDto save(String correlationId, String logKey, ProductDto productDto)
      throws GeneralStatusCodeErrorException;

  /**
   * Update Product
   *
   * @param correlationId String
   * @param logKey String
   * @param productDto ProductDto
   * @param id long
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  void update(String correlationId, String logKey, ProductDto productDto, long id)
      throws GeneralStatusCodeErrorException;

  /**
   * Delete Product
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  void delete(String correlationId, String logKey, long id) throws GeneralStatusCodeErrorException;

  /**
   * Find Product By Id
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return ProductDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  ProductDto findById(String correlationId, String logKey, long id)
      throws GeneralStatusCodeErrorException;

  /**
   * Find All Products
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param search String
   * @param category String
   * @return PageResponse<ProductDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  PageResponse<ProductDto> findAll(
      String correlationId, String logKey, int page, int size, String search, String category)
      throws GeneralStatusCodeErrorException;

  /**
   * Get low stock products
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ProductDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  List<ProductDto> getLowStockAlerts(
      String correlationId,
      String logKey)
      throws GeneralStatusCodeErrorException;

  /**
   * Get all products without pagination
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ProductDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  List<ProductDto> getAllProducts(
      String correlationId,
      String logKey)
      throws GeneralStatusCodeErrorException;
}
