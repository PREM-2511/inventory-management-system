package com.stockiq.inventory.dto;

import com.stockiq.inventory.common.enums.supplier.SupplierStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDto {
  private Long id;

  @NotBlank(message = "Supplier name is required")
  private String name;

  @NotBlank(message = "Category is required")
  private String category;

  private SupplierStatus status;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "Phone is required")
  private String phone;

  private String createdAt;
  private String createdBy;
  private String updatedAt;
  private String updatedBy;
}
