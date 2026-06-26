package com.stockiq.inventory.common.response.category;

import com.stockiq.inventory.common.enums.category.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDetails {
  private Long id;
  private String name;
  private String icon;
  private Status status;
}
