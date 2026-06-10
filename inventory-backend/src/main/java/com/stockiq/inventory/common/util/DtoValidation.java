package com.stockiq.inventory.common.util;

import org.springframework.stereotype.Component;

@Component
public class DtoValidation {

  // Category name
  // Include Uppercase, Lowercase, Digits and Spaces
  // Length between 2 and 100 characters
  public static final String CATEGORY_NAME = "^[A-Za-z0-9 ]{2,100}$";

  // Category icon
  // Include Uppercase, Lowercase, Digits, Underscore and Hyphen
  // Length between 2 and 50 characters
  public static final String ICON = "^[A-Za-z0-9_\\- ]{2,50}$";

  // Product name
  // Include Uppercase, Lowercase, Digits and Spaces
  // Length between 2 and 100 characters
  public static final String PRODUCT_NAME = "^[A-Za-z0-9 ]{2,100}$";

  // Supplier name
  // Include Uppercase, Lowercase and Spaces
  // Length between 2 and 100 characters
  public static final String SUPPLIER_NAME = "^[A-Za-z ]{2,100}$";

  // Contact number
  // Include Digits Only
  // Length 10 characters
  public static final String CONTACT_NO = "^[0-9]{10}$";

  // Email address
  // Standard email validation pattern
  public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

  // Address
  // Include Uppercase, Lowercase, Digits, Spaces and Special Characters
  // Length between 2 and 150 characters
  public static final String ADDRESS = "^[A-Za-z0-9 /.\\-,]{2,150}$";

  // Description
  // Include Uppercase, Lowercase, Digits, Spaces and Special Characters
  // Length between 2 and 250 characters
  public static final String DESCRIPTION = "^[A-Za-z0-9 !@#$%?.,\\-]{2,250}$";
}