package com.stockiq.inventory.common.enums.audit;

import lombok.Getter;

@Getter
public enum AuditAction {
  CREATE(1),
  UPDATE(2),
  DELETE(3),
  STOCK_IN(4),
  STOCK_OUT(5);

  private final int value;

  AuditAction(int value) {
    this.value = value;
  }

  // NOTE :- If the enum values are getting increased more than 10,
  //         then we can use a Map<Integer, AuditAction> static collection
  //         for better performance to avoid iterating through all enum values.
  //         VALUE_TO_ACTION_MAP.get(value)

  public static AuditAction fromValue(int value) {
    for (AuditAction action : values()) {
      if (action.getValue() == value) {
        return action;
      }
    }
    throw new IllegalArgumentException("Unknown AuditAction value: " + value);
  }
}
