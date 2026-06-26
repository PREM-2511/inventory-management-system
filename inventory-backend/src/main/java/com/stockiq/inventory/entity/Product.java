package com.stockiq.inventory.entity;

import java.sql.Timestamp;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
  private long id;
  private Long userId;
  private String name;
  private Long categoryId;
  private int qty;
  private int reorderPoint;
  private int status;
  private String createdBy;
  private String updatedBy;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}
