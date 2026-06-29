package com.stockiq.inventory.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditTrailDto {

  private Long id;
  private String userEmail;
  private String action;
  private String entityType;
  private Long entityId;
  private String entityName;
  private String oldValue;
  private String newValue;
  private String createdAt;
}
