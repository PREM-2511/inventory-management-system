package com.stockiq.inventory.service;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.dto.StockDto;
import java.util.List;

public interface StockService {

  /**
   * Log stock movement.
   *
   * @param correlationId String
   * @param logKey String
   * @param movementDto StockMovementDto
   * @param loggedByEmail String
   * @return StockMovementDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  StockDto logMovement(
      String correlationId, String logKey, StockDto movementDto, String loggedByEmail)
      throws GeneralStatusCodeErrorException;

  /**
   * Get stock movement history.
   *
   * @param correlationId String
   * @param logKey String
   * @return List<StockMovementDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  List<StockDto> getHistory(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException;

  /**
   * Get low stock alerts.
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ProductDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  List<ProductDto> getLowStockAlerts(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException;
}
