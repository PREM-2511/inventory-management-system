package com.stockiq.inventory.common.response.user;

import com.stockiq.inventory.common.enums.user.UserRole;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsResponse {

  private long id;
  private String fullName;
  private String companyName;
  private String email;
  private UserRole role;
}
