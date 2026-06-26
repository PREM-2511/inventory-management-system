package com.stockiq.inventory.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

  private SecurityUtils() {
    // Private constructor to prevent instantiation
  }

  /**
   * Safely retrieves the authenticated user's email from the Security Context.
   *
   * @return String representing the user's email, or null if not authenticated.
   */
  public static String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
      return authentication.getName();
    }
    return null;
  }

  /**
   * Safely retrieves the authenticated user's database ID from the Security Context.
   *
   * @return String representing the user's database ID, or null if not authenticated.
   */
  public static String getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof CustomUserPrincipal) {
        return String.valueOf(((CustomUserPrincipal) principal).getUserId());
      }
    }
    return null;
  }

  /**
   * Safely retrieves the authenticated user's database ID as a Long from the Security Context.
   *
   * @return Long representing the user's database ID, or null if not authenticated.
   */
  public static Long getCurrentUserIdAsLong() {
    String userIdStr = getCurrentUserId();
    if (userIdStr != null) {
      try {
        return Long.valueOf(userIdStr);
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
  }
}
