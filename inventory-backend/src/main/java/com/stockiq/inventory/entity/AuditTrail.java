package com.stockiq.inventory.entity;

import java.sql.Timestamp;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditTrail {

  private long id;
  private Long userId;
  private String userEmail;
  private int action;
  private int entityType;
  private Long entityId;
  private String entityName;
  private String oldValue;
  private String newValue;
  private Timestamp createdAt;
}
