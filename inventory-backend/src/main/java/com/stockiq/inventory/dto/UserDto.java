package com.stockiq.inventory.dto;

import com.stockiq.inventory.common.enums.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
  private long id;

  @NotBlank(message = "Full name is required")
  private String fullName;

  private String companyName;

  @Email(message = "Invalid email")
  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;

  private UserRole role;
  private String createdAt;
  private String createdBy;
  private String updatedAt;
  private String updatedBy;
}