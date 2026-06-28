package com.stockiq.inventory.cron;

import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.service.NotificationService;
import com.stockiq.inventory.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class LowStockNotificationJob {

  private static final String MAIN_LOG_KEY = "CRON_LOW_STOCK_NOTIFICATIONS";
  private static final String COMPONENT = "cron_component";
  private static final String INIT_LOG = "init_log";
  private static final String END_LOG = "end_log";

  private final ProductService productService;
  private final NotificationService notificationService;

  public LowStockNotificationJob(
      ProductService productService, NotificationService notificationService) {
    this.productService = productService;
    this.notificationService = notificationService;
  }

  @Scheduled(cron = "${app.notification.low-stock-cron}")
  public void execute() {
    String correlationId = "CRON_LOW_" + System.currentTimeMillis();
    ActivityLog.logInfo(correlationId, MAIN_LOG_KEY, COMPONENT, INIT_LOG);

    try {
      // Retrieve all low stock products across all users
      List<ProductDto> lowStockProducts =
          productService.getAllLowStockProducts(correlationId, MAIN_LOG_KEY);

      //  Process each product to verify duplicate warnings and create notifications
      for (ProductDto product : lowStockProducts) {
        if (product.getUserId() == null) {
          continue;
        }

        notificationService.checkAndCreateLowStockNotification(
            correlationId,
            MAIN_LOG_KEY,
            product.getUserId(),
            product.getName(),
            product.getQty(),
            product.getReorder());
      }

    } catch (Exception e) {
      ActivityLog.logError(correlationId, MAIN_LOG_KEY, COMPONENT, "exception_log", e.getMessage());
    }

    ActivityLog.logInfo(correlationId, MAIN_LOG_KEY, COMPONENT, END_LOG);
  }
}
