package com.stockiq.inventory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordDto {

  @Email(message = "Invalid email")
  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "New password is required")
  private String newPassword;
}
