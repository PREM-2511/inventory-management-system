package com.stockiq.inventory.controller;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.supplier.CreateSupplierResponse;
import com.stockiq.inventory.common.response.supplier.DeleteSupplierResponse;
import com.stockiq.inventory.common.response.supplier.SupplierDetails;
import com.stockiq.inventory.common.response.supplier.UpdateSupplierResponse;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.common.enums.supplier.SupplierStatus;
import com.stockiq.inventory.dto.SupplierDto;
import com.stockiq.inventory.service.SupplierService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

  private static final String MAIN_LOG_KEY = "SUPPLIER_";
  private static final String X_CORRELATION_ID = "X-Correlation-Id";
  private static final String INIT_LOG = "init_log";
  private static final String CONTROLLER = "controller";

  private final SupplierService supplierService;

  public SupplierController(SupplierService supplierService) {
    this.supplierService = supplierService;
  }

  /**
   * Create a new supplier.
   *
   * @param supplierDto SupplierDto
   * @param correlationId String
   * @return ResponseEntity<CreateSupplierResponse>
   */
  @PostMapping
  public ResponseEntity<CreateSupplierResponse> create(
      @RequestBody @Valid SupplierDto supplierDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "CREATE";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    SupplierDto saved = supplierService.save(correlationId, logKey, supplierDto);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.CREATE.getValue()))
        .body(
            CreateSupplierResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .category(saved.getCategory())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build());
  }

  /**
   * Find all suppliers with pagination and optional search.
   *
   * @param correlationId String
   * @param page int
   * @param size int
   * @param search String
   * @return ResponseEntity<PageResponse<SupplierDto>>
   */
  @GetMapping
  public ResponseEntity<PageResponse<SupplierDetails>> list(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "", required = false) String search)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "FIND_ALL";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(supplierService.findAll(correlationId, logKey, page, size, search));
  }

  /**
   * Find supplier by id.
   *
   * @param id long
   * @param correlationId String
   * @return ResponseEntity<SupplierDto>
   */
  @GetMapping("/{id}")
  public ResponseEntity<SupplierDetails> getById(
      @PathVariable long id,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "FIND_BY_ID";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(supplierService.findById(correlationId, logKey, id));
  }

  /**
   * Update supplier.
   *
   * @param id long
   * @param supplierDto SupplierDto
   * @param correlationId String
   * @return ResponseEntity<UpdateSupplierResponse>
   */
  @PutMapping("/{id}")
  public ResponseEntity<UpdateSupplierResponse> update(
      @PathVariable long id,
      @RequestBody @Valid SupplierDto supplierDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "UPDATE";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    supplierService.update(correlationId, logKey, supplierDto, id);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(UpdateSupplierResponse.builder().message("Supplier updated successfully").build());
  }

  /**
   * Delete supplier.
   *
   * @param id long
   * @param correlationId String
   * @return ResponseEntity<DeleteSupplierResponse>
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<DeleteSupplierResponse> delete(
      @PathVariable long id,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "DELETE";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    supplierService.delete(correlationId, logKey, id);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(DeleteSupplierResponse.builder().message("Supplier deleted successfully").build());
  }

  /**
   * Get all available supplier statuses.
   *
   * @return ResponseEntity<List<Map<String, Object>>>
   */
  @GetMapping("/statuses")
  public ResponseEntity<List<Map<String, Object>>> getStatuses() {
    List<Map<String, Object>> list = new ArrayList<>();
    for (SupplierStatus status : SupplierStatus.values()) {
      Map<String, Object> map = new HashMap<>();
      map.put("name", status.name());
      map.put("value", status.getValue());
      map.put("displayName", status.toValue());
      list.add(map);
    }
    return ResponseEntity.ok(list);
  }
}
