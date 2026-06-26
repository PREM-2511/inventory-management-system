package com.stockiq.inventory.common.enums.category;

import lombok.Getter;

@Getter
public enum Status {
  ACTIVE(1),
  INACTIVE(0);

  private final int value;

  Status(int value) {
    this.value = value;
  }

  // NOTE :- If the status values are getting increased more than 10,
  //         then we can use a Map<Integer, Status> static collection
  //         for better performance to avoid iterating through all enum values.
  //         VALUE_TO_STATUS_MAP.get(value)

  public static Status fromValue(int value) {
    for (Status status : values()) {
      if (status.getValue() == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown Status value: " + value);
  }
}
