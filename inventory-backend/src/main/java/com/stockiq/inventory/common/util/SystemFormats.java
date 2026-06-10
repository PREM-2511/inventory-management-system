package com.stockiq.inventory.common.util;

import org.springframework.stereotype.Component;

/**
 * System format constants used throughout the application.
 */
@Component
public class SystemFormats {

  // Date and time format
  // Example: 2026-06-05 14:30:45
  public static final String DATE_TIME_FORMAT_1 =
      "yyyy-MM-dd HH:mm:ss";

  // Date format
  // Example: 2026-06-05
  public static final String DATE_FORMAT_1 =
      "yyyy-MM-dd";

  // Time format
  // Example: 14:30:45
  public static final String TIME_FORMAT_1 =
      "HH:mm:ss";
}