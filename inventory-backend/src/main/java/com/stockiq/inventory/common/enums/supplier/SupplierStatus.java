package com.stockiq.inventory.common.enums.supplier;

import lombok.Getter;

@Getter
public enum SupplierStatus {
  ACTIVE(1),
  INACTIVE(0),
  PENDING(2),
  SUSPENDED(3);

  private final int value;

  SupplierStatus(int value) {
    this.value = value;
  }

  // NOTE :- If the status values are getting increased more than 10,
  //         then we can use a Map<Integer, SupplierStatus> static collection
  //         for better performance to avoid iterating through all enum values.
  //         VALUE_TO_STATUS_MAP.get(value)
  public static SupplierStatus fromValue(int value) {

    for (SupplierStatus status : values()) {
      if (status.getValue() == value) {
        return status;
      }
    }

    throw new IllegalArgumentException("Unknown SupplierStatus value: " + value);
  }
}
