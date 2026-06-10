package com.stockiq.inventory.entity;

import java.sql.Timestamp;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
  private long id;
  private String fullName;
  private String companyName;
  private String email;
  private String password;
  private long roleId;
  private String createdBy;
  private String updatedBy;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}