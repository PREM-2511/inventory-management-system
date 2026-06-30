package com.stockiq.inventory.common.response.supplier;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteSupplierResponse {
  private String message;
}
