package com.stockiq.inventory.service.impl;

import com.stockiq.inventory.common.enums.ErrorMessageCodeEnum;
import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.category.Status;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.category.CategoryDetails;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.CategoryDto;
import com.stockiq.inventory.entity.Category;
import com.stockiq.inventory.repository.CategoryRepository;
import com.stockiq.inventory.service.CategoryService;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

  private static final String SERVICE_IMPL = "category_service_impl";

  private static final String INIT_LOG = "init_log";

  private static final String EXCEPTION_LOG = "exception_log";

  private static final String DUPLICATE_EXCEPTION_LOG = "duplicate_exception_log";

  private final CategoryRepository categoryRepository;

  public CategoryServiceImpl(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  /**
   * Save Category.
   *
   * @param correlationId String
   * @param logKey String
   * @param categoryDto CategoryDto
   * @return CategoryDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public CategoryDto save(String correlationId, String logKey, CategoryDto categoryDto)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      Category category = toEntity(categoryDto);

      category.setStatus(
          categoryDto.getStatus() != null
              ? categoryDto.getStatus().getValue()
              : Status.ACTIVE.getValue());

      category.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

      long id = categoryRepository.save(correlationId, logKey, category);

      categoryDto.setId(id);
      categoryDto.setStatus(Status.fromValue(category.getStatus()));
      categoryDto.setCreatedAt(category.getCreatedAt().toString());

      return categoryDto;

    } catch (DuplicateKeyException e) {

      ActivityLog.logError(
          correlationId, logKey, SERVICE_IMPL, DUPLICATE_EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Category name already exists",
              "Category Create Failed",
              HttpStatus.BAD_REQUEST,
              HttpStatusCodeEnum.BAD_REQUEST.getValue(),
              ErrorMessageCodeEnum.CATEGORY_CREATE_FAILED.getCode(),
              new Date().toString(),
              null));

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to create category",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Update Category.
   *
   * @param correlationId String
   * @param logKey String
   * @param categoryDto CategoryDto
   * @param id long
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public void update(String correlationId, String logKey, CategoryDto categoryDto, long id)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      CategoryDto existingCategory = findById(correlationId, logKey, id);

      Category category = toEntity(categoryDto);

      category.setId(id);
      category.setName(categoryDto.getName());
      category.setIcon(categoryDto.getIcon());

      category.setStatus(
          categoryDto.getStatus() != null
              ? categoryDto.getStatus().getValue()
              : existingCategory.getStatus().getValue());

      category.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

      int updated = categoryRepository.update(correlationId, logKey, category);

      if (updated == 0) {

        throw new GeneralStatusCodeErrorException(
            new ErrorMessage(
                "Category not found",
                "Category Update Failed",
                HttpStatus.NOT_FOUND,
                HttpStatusCodeEnum.NOT_FOUND.getValue(),
                ErrorMessageCodeEnum.CATEGORY_NOT_FOUND.getCode(),
                new Date().toString(),
                null));
      }

    } catch (DuplicateKeyException e) {

      ActivityLog.logError(
          correlationId, logKey, SERVICE_IMPL, DUPLICATE_EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Category name already exists",
              "Category Update Failed",
              HttpStatus.BAD_REQUEST,
              HttpStatusCodeEnum.BAD_REQUEST.getValue(),
              ErrorMessageCodeEnum.CATEGORY_UPDATE_FAILED.getCode(),
              new Date().toString(),
              null));

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to update category",
              "Category Update Failed",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.CATEGORY_UPDATE_FAILED.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Delete Category.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public void delete(String correlationId, String logKey, long id)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      int deleted = categoryRepository.delete(correlationId, logKey, id);

      if (deleted == 0) {

        throw new GeneralStatusCodeErrorException(
            new ErrorMessage(
                "Category not found",
                "Category Delete Failed",
                HttpStatus.NOT_FOUND,
                HttpStatusCodeEnum.NOT_FOUND.getValue(),
                ErrorMessageCodeEnum.CATEGORY_NOT_FOUND.getCode(),
                new Date().toString(),
                null));
      }

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to delete category",
              "Category Delete Failed",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.CATEGORY_DELETE_FAILED.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Find Category By Id.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return CategoryDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public CategoryDto findById(String correlationId, String logKey, long id)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      return categoryRepository
          .findById(correlationId, logKey, id)
          .orElseThrow(
              () ->
                  new GeneralStatusCodeErrorException(
                      new ErrorMessage(
                          "Category not found",
                          "Category Not Found",
                          HttpStatus.NOT_FOUND,
                          HttpStatusCodeEnum.NOT_FOUND.getValue(),
                          ErrorMessageCodeEnum.CATEGORY_NOT_FOUND.getCode(),
                          new Date().toString(),
                          null)));

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to find category",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Find All Categories With Pagination.
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param search String
   * @param status Status
   * @return PageResponse<CategoryDetails>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public PageResponse<CategoryDetails> findAll(
      String correlationId, String logKey, int page, int size, String search, Status status)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      List<CategoryDetails> categories =
          categoryRepository.findAll(correlationId, logKey, page, size, search, status).stream()
              .map(this::toDto)
              .toList();

      long total = categoryRepository.countAll(correlationId, logKey, search, status);

      return new PageResponse<>(categories, page, size, total);

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to find categories",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Find Category By Name.
   *
   * @param correlationId String
   * @param logKey String
   * @param name String
   * @return Category
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public Category findByName(String correlationId, String logKey, String name)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      return categoryRepository
          .findByName(correlationId, logKey, name)
          .orElseThrow(
              () ->
                  new GeneralStatusCodeErrorException(
                      new ErrorMessage(
                          "Category not found",
                          "Category Not Found",
                          HttpStatus.NOT_FOUND,
                          HttpStatusCodeEnum.NOT_FOUND.getValue(),
                          ErrorMessageCodeEnum.CATEGORY_NOT_FOUND.getCode(),
                          new Date().toString(),
                          null)));

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to find category",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Convert CategoryDto to Category entity.
   *
   * @param dto CategoryDto
   * @return Category
   */
  private Category toEntity(CategoryDto dto) {
    return Category.builder()
        .id(dto.getId() != null ? dto.getId() : 0L)
        .name(dto.getName())
        .icon(dto.getIcon())
        .createdBy(dto.getCreatedBy())
        .updatedBy(dto.getUpdatedBy())
        .build();
  }

  /**
   * Convert Category entity to CategoryDto.
   *
   * @param category Category
   * @return CategoryDto
   */
  private CategoryDetails toDto(Category category) {
    return CategoryDetails.builder()
        .id(category.getId())
        .name(category.getName())
        .icon(category.getIcon())
        .status(Status.fromValue(category.getStatus()))
        .build();
  }
}
