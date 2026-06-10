package com.stockiq.inventory.common.response.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteCategoryResponse {
  private String message;
}
