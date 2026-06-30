package com.stockiq.inventory.common.response.notification;

import com.stockiq.inventory.common.enums.notification.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateNotificationResponse {

  private Long id;
  private String title;
  private String message;
  private NotificationType type;
  private Boolean isRead;
  private String createdAt;
}
