package com.stockiq.inventory.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardCategoryDto {
  private String name;
  private long count;
  private int percentage;
}
