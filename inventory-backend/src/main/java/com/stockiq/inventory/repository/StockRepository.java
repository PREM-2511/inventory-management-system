package com.stockiq.inventory.repository;

import com.stockiq.inventory.dto.StockDto;
import com.stockiq.inventory.entity.StockTransaction;
import java.util.List;

public interface StockRepository {

  /**
   * Save stock transaction.
   *
   * @param correlationId String
   * @param logKey String
   * @param transaction StockTransaction
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  long save(String correlationId, String logKey, StockTransaction transaction) throws Exception;

  /**
   * Get all stock movement history.
   *
   * @param correlationId String
   * @param logKey String
   * @return List<StockMovementDto>
   * @throws Exception if an error occurs during the operation
   */
  List<StockDto> findAll(String correlationId, String logKey) throws Exception;

  /**
   * Count all stock transactions.
   *
   * @param correlationId String
   * @param logKey String
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  long countAll(String correlationId, String logKey) throws Exception;
}
