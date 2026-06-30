package com.stockiq.inventory.dto;

import com.stockiq.inventory.common.enums.category.Status;
import com.stockiq.inventory.common.util.DtoValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

  private Long id;

  @NotBlank(message = "Category name is required")
  @Pattern(
      regexp = DtoValidation.CATEGORY_NAME,
      message = "Category name must be between 2 and 100 characters")
  private String name;

  @Pattern(regexp = DtoValidation.ICON, message = "Invalid icon format")
  private String icon;

  private Status status;
  private String createdAt;
  private String createdBy;
  private String updatedAt;
  private String updatedBy;
}
