package com.stockiq.inventory.common.enums.user;

import lombok.Getter;

@Getter
public enum UserRole {
  ADMIN(1),
  USER(2);

  private final long value;

  UserRole(long value) {
    this.value = value;
  }

  // NOTE :- If the status values are getting increased more than 10,
  //         then we can use a Map<Integer, Status> static collection
  //         for better performance to avoid iterating through all enum values.
  //         VALUE_TO_STATUS_MAP.get(value)

  public static UserRole fromValue(long value) {
    for (UserRole role : values()) {
      if (role.getValue() == value) {
        return role;
      }
    }
    throw new IllegalArgumentException("Unknown UserRole value: " + value);
  }
}