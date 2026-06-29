package com.stockiq.inventory.common.enums.audit;

import lombok.Getter;

@Getter
public enum AuditEntityType {
  PRODUCT(1),
  CATEGORY(2),
  SUPPLIER(3),
  STOCK(4);

  private final int value;

  AuditEntityType(int value) {
    this.value = value;
  }

  // NOTE :- If the enum values are getting increased more than 10,
  //         then we can use a Map<Integer, AuditEntityType> static collection
  //         for better performance to avoid iterating through all enum values.
  //         VALUE_TO_ENTITY_TYPE_MAP.get(value)

  public static AuditEntityType fromValue(int value) {
    for (AuditEntityType type : values()) {
      if (type.getValue() == value) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown AuditEntityType value: " + value);
  }
}
