package com.stockiq.inventory.common.enums.stock;

import lombok.Getter;

@Getter
public enum StockLevel {
  NORMAL(1),
  LOW(2),
  CRITICAL(3),
  OUT_OF_STOCK(4);

  private final int value;

  StockLevel(int value) {
    this.value = value;
  }

  // NOTE :- If values increase significantly,
  // use a static Map<Integer, StockLevel> for better performance.
  public static StockLevel fromValue(int value) {

    for (StockLevel level : values()) {
      if (level.getValue() == value) {
        return level;
      }
    }

    throw new IllegalArgumentException("Unknown StockLevel value: " + value);
  }

  /**
   * Determine stock level based on quantity and reorder point.
   *
   * @param qty current quantity
   * @param reorderPoint reorder level
   * @return StockLevel
   */
  public static StockLevel fromQty(int qty, int reorderPoint) {

    if (qty <= 0) {
      return OUT_OF_STOCK;
    }

    if (qty <= reorderPoint / 2) {
      return CRITICAL;
    }

    if (qty <= reorderPoint) {
      return LOW;
    }

    return NORMAL;
  }
}
