package com.stockiq.inventory.common.response.user;

import com.stockiq.inventory.common.enums.user.UserRole;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFullDetailsResponse {

  private long id;
  private String fullName;
  private String companyName;
  private String email;
  private UserRole role;
  private String createdAt;
  private String createdBy;
  private String updatedAt;
  private String updatedBy;
}
