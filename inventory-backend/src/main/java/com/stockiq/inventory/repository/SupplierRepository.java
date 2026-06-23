package com.stockiq.inventory.repository;

import com.stockiq.inventory.entity.Supplier;
import java.util.List;
import java.util.Optional;

public interface SupplierRepository {

  /**
   * Save Supplier.
   *
   * @param correlationId String
   * @param logKey String
   * @param supplier Supplier
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  long save(String correlationId, String logKey, Supplier supplier) throws Exception;

  /**
   * Update Supplier.
   *
   * @param correlationId String
   * @param logKey String
   * @param supplier Supplier
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  int update(String correlationId, String logKey, Supplier supplier) throws Exception;

  /**
   * Delete Supplier.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  int delete(String correlationId, String logKey, long id) throws Exception;

  /**
   * Find Supplier By id.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return Optional<Supplier>
   * @throws Exception if an error occurs during the operation
   */
  Optional<Supplier> findById(String correlationId, String logKey, long id) throws Exception;

  /**
   * Find All Suppliers With Pagination.
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param search String
   * @return List<Supplier>
   * @throws Exception if an error occurs during the operation
   */
  List<Supplier> findAll(String correlationId, String logKey, int page, int size, String search)
      throws Exception;

  /**
   * Count All Suppliers.
   *
   * @param correlationId String
   * @param logKey String
   * @param search String
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  long countAll(String correlationId, String logKey, String search) throws Exception;
}
