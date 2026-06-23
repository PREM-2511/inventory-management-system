package com.stockiq.inventory.common.response.supplier;

import com.stockiq.inventory.common.enums.supplier.SupplierStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDetails {

  private Long id;
  private String name;
  private String category;
  private SupplierStatus status;
  private String email;
  private String phone;
}
