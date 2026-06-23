package com.stockiq.inventory.service;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.supplier.SupplierDetails;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.SupplierDto;
import java.util.List;

public interface SupplierService {

  /**
   * Save Supplier.
   *
   * @param correlationId String
   * @param logKey String
   * @param supplierDto SupplierDto
   * @return SupplierDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  SupplierDto save(String correlationId, String logKey, SupplierDto supplierDto)
      throws GeneralStatusCodeErrorException;

  /**
   * Update Supplier.
   *
   * @param correlationId String
   * @param logKey String
   * @param supplierDto SupplierDto
   * @param id long
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  void update(String correlationId, String logKey, SupplierDto supplierDto, long id)
      throws GeneralStatusCodeErrorException;

  /**
   * Delete Supplier.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  void delete(String correlationId, String logKey, long id) throws GeneralStatusCodeErrorException;

  /**
   * Find Supplier By Id.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return SupplierDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  SupplierDetails findById(String correlationId, String logKey, long id)
      throws GeneralStatusCodeErrorException;

  /**
   * Find All Suppliers With Pagination.
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param search String
   * @return PageResponse<SupplierDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  PageResponse<SupplierDetails> findAll(
      String correlationId, String logKey, int page, int size, String search)
      throws GeneralStatusCodeErrorException;
}
