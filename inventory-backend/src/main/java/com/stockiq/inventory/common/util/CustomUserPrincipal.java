package com.stockiq.inventory.common.util;

import java.security.Principal;

public class CustomUserPrincipal implements Principal {
  private final String email;
  private final long userId;

  public CustomUserPrincipal(String email, long userId) {
    this.email = email;
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public long getUserId() {
    return userId;
  }

  @Override
  public String getName() {
    return email;
  }

  @Override
  public String toString() {
    return email;
  }
}
