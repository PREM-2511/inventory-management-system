package com.stockiq.inventory.service.impl;

import com.stockiq.inventory.common.enums.ErrorMessageCodeEnum;
import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.stock.MovementType;
import com.stockiq.inventory.common.enums.stock.StockLevel;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.common.util.SystemFormats;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.dto.StockDto;
import com.stockiq.inventory.entity.Product;
import com.stockiq.inventory.entity.StockTransaction;
import com.stockiq.inventory.repository.ProductRepository;
import com.stockiq.inventory.repository.StockRepository;
import com.stockiq.inventory.service.StockService;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockServiceImpl implements StockService {

  private static final String SERVICE_IMPL = "stock_service_impl";
  private static final String INIT_LOG = "init_log";
  private static final String EXCEPTION_LOG = "exception_log";

  private final ProductRepository productRepository;
  private final StockRepository stockRepository;

  public StockServiceImpl(ProductRepository productRepository, StockRepository stockRepository) {

    this.productRepository = productRepository;
    this.stockRepository = stockRepository;
  }

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
  @Override
  public StockDto logMovement(
      String correlationId, String logKey, StockDto movementDto, String loggedByEmail)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      Product product =
          productRepository
              .findByName(correlationId, logKey, movementDto.getProduct())
              .orElseThrow(
                  () ->
                      new GeneralStatusCodeErrorException(
                          new ErrorMessage(
                              "Stock not found",
                              "Stock Not Found",
                              HttpStatus.NOT_FOUND,
                              HttpStatusCodeEnum.NOT_FOUND.getValue(),
                              ErrorMessageCodeEnum.STOCK_NOT_FOUND.getCode(),
                              new java.util.Date().toString(),
                              null)));

      int newQty;

      if (movementDto.getType() == MovementType.IN) {

        newQty = product.getQty() + movementDto.getQty();

      } else {

        if (product.getQty() < movementDto.getQty()) {

          throw new GeneralStatusCodeErrorException(
              new ErrorMessage(
                  "Insufficient stock quantity",
                  "Stock Update Failed",
                  HttpStatus.BAD_REQUEST,
                  HttpStatusCodeEnum.BAD_REQUEST.getValue(),
                  ErrorMessageCodeEnum.STOCK_UPDATE_FAILED.getCode(),
                  new java.util.Date().toString(),
                  null));
        }

        newQty = product.getQty() - movementDto.getQty();
      }

      product.setQty(newQty);
      product.setUpdatedBy(loggedByEmail);
      product.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

      productRepository.update(correlationId, logKey, product);

      StockTransaction transaction =
          StockTransaction.builder()
              .type(movementDto.getType().getValue())
              .productId(product.getId())
              .qty(movementDto.getQty())
              .createdBy(loggedByEmail)
              .createdAt(Timestamp.valueOf(LocalDateTime.now()))
              .build();

      long transactionId = stockRepository.save(correlationId, logKey, transaction);

      movementDto.setId(transactionId);
      movementDto.setUser(loggedByEmail);
      movementDto.setDate(
          transaction
              .getCreatedAt()
              .toLocalDateTime()
              .format(DateTimeFormatter.ofPattern(SystemFormats.DATE_TIME_FORMAT_1)));

      return movementDto;

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to log stock movement",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.STOCK_UPDATE_FAILED.getCode(),
              new java.util.Date().toString(),
              null));
    }
  }

  /**
   * Get stock movement history.
   *
   * @param correlationId String
   * @param logKey String
   * @return List<StockMovementDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public List<StockDto> getHistory(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      List<StockDto> history = stockRepository.findAll(correlationId, logKey);

      history.forEach(
          dto -> {
            if (dto.getDate() != null && dto.getDate().length() > 19) {
              dto.setDate(dto.getDate().substring(0, 19));
            }
          });

      return history;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to retrieve stock history",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new java.util.Date().toString(),
              null));
    }
  }

  /**
   * Get low stock products.
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ProductDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public List<ProductDto> getLowStockAlerts(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      List<ProductDto> products = productRepository.getLowStockAlerts(correlationId, logKey);

      products.forEach(
          dto -> dto.setStockLevel(StockLevel.fromQty(dto.getQty(), dto.getReorder())));

      return products;

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to get low stock products",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new java.util.Date().toString(),
              null));
    }
  }
}
