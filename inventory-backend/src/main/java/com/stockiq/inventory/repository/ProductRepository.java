package com.stockiq.inventory.repository;

import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

  /**
   * Save Product
   *
   * @param correlationId String
   * @param logKey String
   * @param product Product
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  long save(String correlationId, String logKey, Product product) throws Exception;

  /**
   * Update Product
   *
   * @param correlationId String
   * @param logKey String
   * @param product Product
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  int update(String correlationId, String logKey, Product product) throws Exception;

  /**
   * Delete Product
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  int delete(String correlationId, String logKey, long id) throws Exception;

  /**
   * Find Product By Id
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return Optional<ProductDto>
   * @throws Exception if an error occurs during the operation
   */
  Optional<ProductDto> findById(String correlationId, String logKey, long id) throws Exception;

  /**
   * Find Product By Name
   *
   * @param correlationId String
   * @param logKey String
   * @param name String
   * @return Optional<Product>
   * @throws Exception if an error occurs during the operation
   */
  Optional<Product> findByName(String correlationId, String logKey, String name) throws Exception;

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
   * @throws Exception if an error occurs during the operation
   */
  List<ProductDto> findAll(
      String correlationId, String logKey, int page, int size, String search, String category)
      throws Exception;

  /**
   * Count Products
   *
   * @param correlationId String
   * @param logKey String
   * @param search String
   * @param category String
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  long countAll(String correlationId, String logKey, String search, String category)
      throws Exception;

  /**
   * Get Low Stock Alerts
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ProductDto>
   * @throws Exception if an error occurs during the operation
   */
  List<ProductDto> getLowStockAlerts(String correlationId, String logKey) throws Exception;

  /**
   * Get all low stock products across all users (no SecurityContext — for background jobs only).
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ProductDto>
   * @throws Exception if an error occurs during the operation
   */
  List<ProductDto> getAllLowStockProducts(String correlationId, String logKey) throws Exception;
}
