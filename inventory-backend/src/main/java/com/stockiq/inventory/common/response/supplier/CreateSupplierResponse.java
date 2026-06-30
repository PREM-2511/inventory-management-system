package com.stockiq.inventory.common.response.supplier;

import com.stockiq.inventory.common.enums.supplier.SupplierStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSupplierResponse {
  private long id;
  private String name;
  private String category;
  private String email;
  private String phone;
  private SupplierStatus status;
  private String createdAt;
}
