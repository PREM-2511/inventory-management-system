package com.stockiq.inventory.dto;

import com.stockiq.inventory.common.enums.product.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
  private Long id;

  @NotBlank(message = "Product name is required")
  private String name;

  @NotBlank(message = "Category is required")
  private String category;

  @NotNull(message = "Quantity is required")
  @Min(value = 0, message = "Quantity cannot be negative")
  private Integer qty;

  @NotNull(message = "Reorder point is required")
  @Min(value = 0, message = "Reorder point cannot be negative")
  private Integer reorder;

  private ProductStatus status;

  private String createdAt;
  private String createdBy;
  private String updatedAt;
  private String updatedBy;
}
