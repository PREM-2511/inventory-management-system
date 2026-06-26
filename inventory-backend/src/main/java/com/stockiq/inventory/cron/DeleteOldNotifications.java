package com.stockiq.inventory.cron;

import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class DeleteOldNotifications {

  private static final String MAIN_LOG_KEY = "CRON_DELETE_OLD_NOTIFICATIONS";
  private static final String COMPONENT = "cron_component";
  private static final String INIT_LOG = "init_log";
  private static final String END_LOG = "end_log";

  private static final String TOTAL_COUNT_LOG = "total_count - ";
  private static final String CURRENT_COUNT_LOG = " | current_count - ";

  private final NotificationService notificationService;

  @Value("${app.notification.delete-days}")
  private int deleteDays;

  @Value("${app.notification.batch-size}")
  private int batchSize;

  @Value("${app.notification.time-zone}")
  private String timeZone;

  @Value("${app.notification.date-time-format}")
  private String dateTimeFormat;

  public DeleteOldNotifications(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @Scheduled(cron = "${app.notification.cron}")
  public void execute() {

    String correlationId = "CRON_" + System.currentTimeMillis();

    ActivityLog.logInfo(correlationId, MAIN_LOG_KEY, COMPONENT, INIT_LOG);

    int totalCount = 0;
    int currentCount = 0;

    try {

      LocalDateTime now = LocalDateTime.now(ZoneId.of(timeZone));

      LocalDateTime cutoffDate = now.minusDays(deleteDays);

      String cutoffDateStr = cutoffDate.format(DateTimeFormatter.ofPattern(dateTimeFormat));

      totalCount =
          notificationService.countOldNotifications(correlationId, MAIN_LOG_KEY, cutoffDateStr);

      ActivityLog.logInfo(correlationId, MAIN_LOG_KEY, COMPONENT, "cutoffDate - " + cutoffDateStr);

      if (totalCount == 0) {

        ActivityLog.logInfo(correlationId, MAIN_LOG_KEY, COMPONENT, "No old notifications found.");

        return;
      }

      do {

        notificationService.deleteOldNotificationsBatch(
            correlationId, MAIN_LOG_KEY, cutoffDateStr, batchSize);

        currentCount += batchSize;

        ActivityLog.logInfo(
            correlationId,
            MAIN_LOG_KEY,
            COMPONENT,
            TOTAL_COUNT_LOG + totalCount + CURRENT_COUNT_LOG + currentCount);

      } while (currentCount < totalCount);

    } catch (Exception e) {

      ActivityLog.logError(correlationId, MAIN_LOG_KEY, COMPONENT, "exception_log", e.getMessage());
    }

    ActivityLog.logInfo(correlationId, MAIN_LOG_KEY, COMPONENT, END_LOG);
  }
}

/*
* # Delete notifications after 30 days
notification.delete.days=30

# Delete 100 notifications at a time
notification.delete.batch-size=100

# Run every day at 4:00 AM
notification.delete.cron=0 0 4 * * *

# Time zone
application.time-zone=Asia/Colombo

# Date format
application.date-time-format=yyyy-MM-dd HH:mm:ss
*
*/
