package com.stockiq.inventory.common.enums.notification;

import lombok.Getter;

@Getter
public enum NotificationType {
  INFO(1),
  WARNING(2),
  ERROR(3),
  SUCCESS(4);

  private final int value;

  NotificationType(int value) {
    this.value = value;
  }

  /**
   * Convert integer value to NotificationType.
   *
   * @param value notification type value
   * @return NotificationType
   */
  public static NotificationType fromValue(int value) {

    for (NotificationType type : values()) {
      if (type.getValue() == value) {
        return type;
      }
    }

    throw new IllegalArgumentException("Unknown NotificationType value : " + value);
  }
}
