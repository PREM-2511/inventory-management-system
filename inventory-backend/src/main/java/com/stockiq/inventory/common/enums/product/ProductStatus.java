package com.stockiq.inventory.common.enums.product;

import lombok.Getter;

@Getter
public enum ProductStatus {
  IN_STOCK(1),
  LOW_STOCK(2),
  CRITICAL(3),
  OUT_OF_STOCK(4);

  private final int value;

  ProductStatus(int value) {
    this.value = value;
  }

  // NOTE :- If the status values are getting increased more than 10,
  //         then we can use a Map<Integer, ProductStatus> static collection
  //         for better performance to avoid iterating through all enum values.
  //         VALUE_TO_STATUS_MAP.get(value)

  public static ProductStatus fromValue(int value) {
    for (ProductStatus status : values()) {
      if (status.getValue() == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown ProductStatus value: " + value);
  }
}
