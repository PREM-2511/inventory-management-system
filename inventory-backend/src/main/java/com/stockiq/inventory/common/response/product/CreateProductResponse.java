package com.stockiq.inventory.common.response.product;

import com.stockiq.inventory.common.enums.product.ProductStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateProductResponse {
  private long id;
  private String name;
  private String category;
  private Integer qty; // Quantity
  private Integer reorder;
  private ProductStatus status;
  private String createdAt;
}
