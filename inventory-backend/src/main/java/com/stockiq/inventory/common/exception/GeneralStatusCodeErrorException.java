package com.stockiq.inventory.common.exception;

import com.stockiq.inventory.common.util.ErrorMessage;

/** Exception class to handle custom error messages. */
public class GeneralStatusCodeErrorException extends Exception {

  private final ErrorMessage errorMessage;

  /**
   * Constructs a new GeneralStatusCodeErrorException.
   *
   * @param errorMessage ErrorMessage
   */
  public GeneralStatusCodeErrorException(ErrorMessage errorMessage) {
    super(errorMessage.getTitle());
    this.errorMessage = errorMessage;
  }

  /**
   * Retrieves the error message associated with this exception.
   *
   * @return ErrorMessage
   */
  public ErrorMessage getApiError() {
    return errorMessage;
  }
}
