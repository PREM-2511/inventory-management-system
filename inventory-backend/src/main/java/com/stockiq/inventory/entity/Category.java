package com.stockiq.inventory.entity;

import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
  private long id;
  private String name;
  private String icon;
  private int status;
  private String createdBy;
  private String updatedBy;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}
