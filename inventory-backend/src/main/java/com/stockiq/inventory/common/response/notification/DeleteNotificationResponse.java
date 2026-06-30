package com.stockiq.inventory.common.response.notification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteNotificationResponse {

  private String message;
}
