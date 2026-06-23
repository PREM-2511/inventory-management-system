package com.stockiq.inventory.common.response.stock;

import com.stockiq.inventory.common.enums.stock.MovementType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogMovementResponse {
  private long id;
  private MovementType type;
  private String product;
  private Integer qty; // Quantity
  private String date;
  private String user;
}
