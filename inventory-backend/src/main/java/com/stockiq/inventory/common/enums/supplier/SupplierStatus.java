package com.stockiq.inventory.common.enums.supplier;

import com.fasterxml.jackson.annotation.JsonCreator;
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

  // NOTE :- Supplier DB column stores status as a String (e.g. "Active", "Inactive").
  //         These two methods handle conversion between the enum and the DB string value.

  public String toValue() {
    return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
  }

  @JsonCreator
  public static SupplierStatus fromString(String value) {
    if (value == null) {
      return null;
    }
    for (SupplierStatus status : values()) {
      if (status.name().equalsIgnoreCase(value.trim())
          || String.valueOf(status.getValue()).equals(value.trim())
          || status.toValue().equalsIgnoreCase(value.trim())) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown SupplierStatus value: " + value);
  }
}
