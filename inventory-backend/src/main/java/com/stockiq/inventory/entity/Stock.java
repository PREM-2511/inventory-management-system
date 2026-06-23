package com.stockiq.inventory.entity;

import java.sql.Timestamp;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {
  private long id;
  private int type; // 'IN' or 'OUT'
  private long productId;
  private int qty;
  private String createdBy;
  private Timestamp createdAt;
}
