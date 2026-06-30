package com.stockiq.inventory.dto;

import com.stockiq.inventory.common.enums.stock.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDto {
  private Long id;

  @NotNull(message = "Movement type is required")
  private MovementType type; // 'IN' or 'OUT'

  @NotBlank(message = "Product name is required")
  private String product; // Product name

  @NotNull(message = "Quantity is required")
  @Min(value = 1, message = "Quantity must be at least 1")
  private Integer qty;

  private String date; // Formatted date & time
  private String user; // Logged by user
}
