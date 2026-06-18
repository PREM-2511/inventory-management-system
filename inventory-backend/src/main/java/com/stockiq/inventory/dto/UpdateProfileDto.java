package com.stockiq.inventory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileDto {

  @NotBlank(message = "Full name is required")
  private String fullName;

  private String companyName;

  @Email(message = "Invalid email")
  @NotBlank(message = "Email is required")
  private String email;
}
