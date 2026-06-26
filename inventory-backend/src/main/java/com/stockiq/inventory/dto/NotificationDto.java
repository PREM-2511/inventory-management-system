package com.stockiq.inventory.dto;

import com.stockiq.inventory.common.enums.notification.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

  private Long id;

  private Long userId;

  @NotBlank(message = "Title is required")
  private String title;

  @NotBlank(message = "Message is required")
  private String message;

  @NotNull(message = "Notification type is required")
  private NotificationType type;

  private Boolean isRead;

  private String createdAt;

}