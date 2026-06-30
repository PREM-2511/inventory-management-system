package com.stockiq.inventory.common.response.category;

import com.stockiq.inventory.common.enums.category.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCategoryResponse {
  private long id;
  private String name;
  private String icon;
  private Status status;
}
