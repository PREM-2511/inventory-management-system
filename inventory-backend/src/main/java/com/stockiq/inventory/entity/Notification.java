package com.stockiq.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

  private Long id;
  private Long userId;
  private String title;
  private String message;
  private String type;
  private Boolean isRead;
  private Timestamp createdAt;
}
