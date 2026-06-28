package com.stockiq.inventory.common.exception;

import com.stockiq.inventory.common.util.ApiError;
import com.stockiq.inventory.common.util.ErrorMessage;

/** Exception class to handle API errors using ApiError enum. */
public class GeneralStatusCodeException extends Exception {

  private final ApiError apiError;

  /**
   * Constructs a new GeneralStatusCodeException.
   *
   * @param apiError ApiError
   */
  public GeneralStatusCodeException(ApiError apiError) {
    super(apiError.getTitle());
    this.apiError = apiError;
  }

  /**
   * Retrieves the error message associated with this exception.
   *
   * @return ErrorMessage
   */
  public ErrorMessage getApiError() {
    return new ErrorMessage(apiError);
  }
}
