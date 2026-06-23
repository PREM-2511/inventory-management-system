package com.stockiq.inventory.common.enums.stock;

import lombok.Getter;

@Getter
public enum MovementType {
  IN(1),
  OUT(0);

  private final int value;

  MovementType(int value) {
    this.value = value;
  }

  // NOTE :- If the values are increased significantly,
  //         consider using a static Map<Integer, MovementType>
  //         for better performance.
  public static MovementType fromValue(int value) {

    for (MovementType type : values()) {
      if (type.getValue() == value) {
        return type;
      }
    }

    throw new IllegalArgumentException("Unknown MovementType value: " + value);
  }
}
