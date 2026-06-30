package com.stockiq.inventory.common.response.user;

import com.stockiq.inventory.common.enums.user.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserResponse {

  private long id;
  private String fullName;
  private String companyName;
  private String email;
  private UserRole role;
}
