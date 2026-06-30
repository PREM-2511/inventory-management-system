package com.stockiq.inventory.controller;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.category.Status;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.category.CategoryDetails;
import com.stockiq.inventory.common.response.category.CreateCategoryResponse;
import com.stockiq.inventory.common.response.category.DeleteCategoryResponse;
import com.stockiq.inventory.common.response.category.UpdateCategoryResponse;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.CategoryDto;
import com.stockiq.inventory.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {

  private static final String MAIN_LOG_KEY = "CATEGORY_";

  private static final String X_CORRELATION_ID = "X-Correlation-Id";

  private static final String INIT_LOG = "init_log";

  private static final String CONTROLLER = "controller";

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  /**
   * Create a new category.
   *
   * @param categoryDto CategoryDto
   * @param correlationId String
   * @return ResponseEntity<CreateCategoryResponse>
   */
  @PostMapping
  public ResponseEntity<CreateCategoryResponse> create(
      @RequestBody @Valid CategoryDto categoryDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "CREATE";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    CategoryDto savedCategory = categoryService.save(correlationId, logKey, categoryDto);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.CREATE.getValue()))
        .body(
            CreateCategoryResponse.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .icon(savedCategory.getIcon())
                .status(savedCategory.getStatus())
                .build());
  }

  /**
   * Find all categories with pagination and filters.
   *
   * @param correlationId String
   * @param page int
   * @param size int
   * @param search String
   * @param status Status
   * @return ResponseEntity<PageResponse<CategoryDetails>>
   */
  @GetMapping
  public ResponseEntity<PageResponse<CategoryDetails>> list(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "", required = false) String search,
      @RequestParam(required = false) Status status)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "FIND_ALL";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(categoryService.findAll(correlationId, logKey, page, size, search, status));
  }

  /**
   * Find category by id.
   *
   * @param id long
   * @param correlationId String
   * @return ResponseEntity<CategoryDto>
   */
  @GetMapping("/{id}")
  public ResponseEntity<CategoryDto> getById(
      @PathVariable long id,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "FIND_BY_ID";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(categoryService.findById(correlationId, logKey, id));
  }

  /**
   * Update category.
   *
   * @param id long
   * @param categoryDto CategoryDto
   * @param correlationId String
   * @return ResponseEntity<UpdateCategoryResponse>
   */
  @PutMapping("/{id}")
  public ResponseEntity<UpdateCategoryResponse> update(
      @PathVariable long id,
      @RequestBody @Valid CategoryDto categoryDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "UPDATE";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    categoryService.update(correlationId, logKey, categoryDto, id);

    return ResponseEntity.ok(
        UpdateCategoryResponse.builder().message("Category updated successfully").build());
  }

  /**
   * Delete category.
   *
   * @param id long
   * @param correlationId String
   * @return ResponseEntity<DeleteCategoryResponse>
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<DeleteCategoryResponse> delete(
      @PathVariable long id,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "DELETE";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    categoryService.delete(correlationId, logKey, id);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(DeleteCategoryResponse.builder().message("Category deleted successfully").build());
  }
}
