package com.stockiq.inventory.common.response.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteProductResponse {
  private String message;
}
