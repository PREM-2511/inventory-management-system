package com.stockiq.inventory.controller;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.product.CreateProductResponse;
import com.stockiq.inventory.common.response.product.DeleteProductResponse;
import com.stockiq.inventory.common.response.product.UpdateProductResponse;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

  private static final String MAIN_LOG_KEY = "PRODUCT_";
  private static final String X_CORRELATION_ID = "X-Correlation-Id";
  private static final String INIT_LOG = "init_log";
  private static final String CONTROLLER = "controller";

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  /**
   * Create a new product.
   *
   * @param productDto ProductDto
   * @param correlationId String
   * @return ResponseEntity<CreateProductResponse>
   */
  @PostMapping
  public ResponseEntity<CreateProductResponse> create(
      @RequestBody @Valid ProductDto productDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "CREATE";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    ProductDto saved = productService.save(correlationId, logKey, productDto);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.CREATE.getValue()))
        .body(
            CreateProductResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .category(saved.getCategory())
                .qty(saved.getQty())
                .reorder(saved.getReorder())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build());
  }

  /**
   * Find all products with pagination, search, and optional category filter.
   *
   * @param correlationId String
   * @param page int
   * @param size int
   * @param search String
   * @param category String
   * @return ResponseEntity<PageResponse<ProductDto>>
   */
  @GetMapping
  public ResponseEntity<PageResponse<ProductDto>> list(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "", required = false) String search,
      @RequestParam(required = false) String category)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "FIND_ALL";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(productService.findAll(correlationId, logKey, page, size, search, category));
  }

  /**
   * Find product by id.
   *
   * @param id long
   * @param correlationId String
   * @return ResponseEntity<ProductDto>
   */
  @GetMapping("/{id}")
  public ResponseEntity<ProductDto> getById(
      @PathVariable long id,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "FIND_BY_ID";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(productService.findById(correlationId, logKey, id));
  }

  /**
   * Update product.
   *
   * @param id long
   * @param productDto ProductDto
   * @param correlationId String
   * @return ResponseEntity<UpdateProductResponse>
   */
  @PutMapping("/{id}")
  public ResponseEntity<UpdateProductResponse> update(
      @PathVariable long id,
      @RequestBody @Valid ProductDto productDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "UPDATE";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    productService.update(correlationId, logKey, productDto, id);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(UpdateProductResponse.builder().message("Product updated successfully").build());
  }

  /**
   * Delete product.
   *
   * @param id long
   * @param correlationId String
   * @return ResponseEntity<DeleteProductResponse>
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<DeleteProductResponse> delete(
      @PathVariable long id,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "DELETE";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    productService.delete(correlationId, logKey, id);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(DeleteProductResponse.builder().message("Product deleted successfully").build());
  }

  /**
   * Low stock
   *
   * @param correlationId String
   * @return ResponseEntity<DeleteProductResponse>
   */
  @GetMapping("/low-stock")
  public ResponseEntity<List<ProductDto>> getLowStockProducts(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "LOW_STOCK";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.ok(
        productService.getLowStockAlerts(correlationId, logKey));
  }

}
