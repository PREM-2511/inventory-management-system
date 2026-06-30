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
import com.stockiq.inventory.entity.Stock;
import com.stockiq.inventory.repository.ProductRepository;
import com.stockiq.inventory.repository.StockRepository;
import com.stockiq.inventory.repository.NotificationRepository;
import com.stockiq.inventory.service.StockService;
import com.stockiq.inventory.service.AuditTrailService;
import com.stockiq.inventory.common.enums.audit.AuditAction;
import com.stockiq.inventory.common.enums.audit.AuditEntityType;
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
  private final NotificationRepository notificationRepository;
  private final AuditTrailService auditTrailService;

  public StockServiceImpl(
      ProductRepository productRepository,
      StockRepository stockRepository,
      NotificationRepository notificationRepository,
      AuditTrailService auditTrailService) {

    this.productRepository = productRepository;
    this.stockRepository = stockRepository;
    this.notificationRepository = notificationRepository;
    this.auditTrailService = auditTrailService;
  }

  /**
   * Log stock movement.
   *
   * @param correlationId String
   * @param logKey String
   * @param movementDto StockMovementDto
   * @return StockMovementDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public StockDto logMovement(
      String correlationId, String logKey, StockDto movementDto, String loggedByUserId)
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

      int oldQty = product.getQty();
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
      product.setUpdatedBy(loggedByUserId);
      product.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

      productRepository.update(correlationId, logKey, product);

      Stock transaction =
          Stock.builder()
              .type(movementDto.getType().getValue())
              .productId(product.getId())
              .qty(movementDto.getQty())
              .createdBy(loggedByUserId)
              .createdAt(Timestamp.valueOf(LocalDateTime.now()))
              .build();

      long transactionId = stockRepository.save(correlationId, logKey, transaction);

      auditTrailService.record(
          correlationId,
          logKey,
          movementDto.getType() == MovementType.IN ? AuditAction.STOCK_IN : AuditAction.STOCK_OUT,
          AuditEntityType.STOCK,
          transactionId,
          product.getName(),
          "Qty=" + oldQty,
          "Qty=" + newQty);

      // Trigger automatic notification based on stock levels
      if (movementDto.getType() == MovementType.IN) {
        try {
          com.stockiq.inventory.entity.Notification stockInNotif =
              com.stockiq.inventory.entity.Notification.builder()
                  .userId(product.getUserId())
                  .title("Stock Restocked")
                  .message(
                      "Restocked "
                          + movementDto.getQty()
                          + " units of '"
                          + product.getName()
                          + "'. New quantity: "
                          + newQty)
                  .type("SUCCESS")
                  .isRead(false)
                  .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                  .build();
          notificationRepository.save(correlationId, logKey, stockInNotif);
        } catch (Exception e) {
          ActivityLog.logError(
              correlationId, logKey, SERVICE_IMPL, "NOTIFICATION_TRIGGER_ERROR", e.getMessage());
        }
      } else if (newQty <= product.getReorderPoint()) {
        try {
          com.stockiq.inventory.entity.Notification lowStockAlert =
              com.stockiq.inventory.entity.Notification.builder()
                  .userId(product.getUserId())
                  .title("Low Stock Warning")
                  .message(
                      "Product '"
                          + product.getName()
                          + "' has reached low stock level ("
                          + newQty
                          + "/"
                          + product.getReorderPoint()
                          + "). Please reorder soon!")
                  .type("WARNING")
                  .isRead(false)
                  .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                  .build();
          notificationRepository.save(correlationId, logKey, lowStockAlert);
        } catch (Exception e) {
          ActivityLog.logError(
              correlationId, logKey, SERVICE_IMPL, "NOTIFICATION_TRIGGER_ERROR", e.getMessage());
        }
      }

      movementDto.setId(transactionId);
      movementDto.setUser(com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserEmail());
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
