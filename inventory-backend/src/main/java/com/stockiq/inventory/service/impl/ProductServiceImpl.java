package com.stockiq.inventory.service.impl;

import com.stockiq.inventory.common.enums.ErrorMessageCodeEnum;
import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.product.ProductStatus;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.entity.Category;
import com.stockiq.inventory.entity.Product;
import com.stockiq.inventory.repository.ProductRepository;
import com.stockiq.inventory.service.CategoryService;
import com.stockiq.inventory.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

  private static final String SERVICE_IMPL = "product_service_impl";
  private static final String INIT_LOG = "init_log";
  private static final String EXCEPTION_LOG = "exception_log";
  private static final String DUPLICATE_EXCEPTION_LOG = "duplicate_exception_log";

  private final ProductRepository productRepository;
  private final CategoryService categoryService;

  public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService) {
    this.productRepository = productRepository;
    this.categoryService = categoryService;
  }

  /**
   * Create Product
   *
   * @param correlationId String
   * @param logKey String
   * @param productDto ProductDto
   * @return ProductDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public ProductDto save(String correlationId, String logKey, ProductDto productDto)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      Category category =
          categoryService.findByName(correlationId, logKey, productDto.getCategory());

      Product product =
          Product.builder()
              .name(productDto.getName())
              .categoryId(category.getId())
              .qty(productDto.getQty())
              .reorderPoint(productDto.getReorder())
              .status(
                  productDto.getStatus() != null
                      ? productDto.getStatus().getValue()
                      : ProductStatus.IN_STOCK.getValue())
              .createdBy(com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserId())
              .createdAt(Timestamp.valueOf(LocalDateTime.now()))
              .build();

      long id = productRepository.save(correlationId, logKey, product);

      productDto.setId(id);
      productDto.setCreatedAt(product.getCreatedAt().toString());

      return productDto;

    } catch (DuplicateKeyException e) {

      ActivityLog.logError(
          correlationId, logKey, SERVICE_IMPL, DUPLICATE_EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Product name already exists",
              "Product Create Failed",
              HttpStatus.BAD_REQUEST,
              HttpStatusCodeEnum.BAD_REQUEST.getValue(),
              ErrorMessageCodeEnum.PRODUCT_CREATE_FAILED.getCode(),
              new Date().toString(),
              null));

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to create product",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Update Product
   *
   * @param correlationId String
   * @param logKey String
   * @param productDto ProductDto
   * @param id long
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public void update(String correlationId, String logKey, ProductDto productDto, long id)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      Category category =
          categoryService.findByName(correlationId, logKey, productDto.getCategory());

      Product product =
          Product.builder()
              .id(id)
              .name(productDto.getName())
              .categoryId(category.getId())
              .qty(productDto.getQty())
              .reorderPoint(productDto.getReorder())
              .status(
                  productDto.getStatus() != null
                      ? productDto.getStatus().getValue()
                      : ProductStatus.IN_STOCK.getValue())
              .updatedBy(com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserId())
              .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
              .build();

      int updated = productRepository.update(correlationId, logKey, product);

      if (updated == 0) {

        throw new GeneralStatusCodeErrorException(
            new ErrorMessage(
                "Product not found",
                "Product Update Failed",
                HttpStatus.NOT_FOUND,
                HttpStatusCodeEnum.NOT_FOUND.getValue(),
                ErrorMessageCodeEnum.PRODUCT_NOT_FOUND.getCode(),
                new Date().toString(),
                null));
      }

    } catch (DuplicateKeyException e) {

      ActivityLog.logError(
          correlationId, logKey, SERVICE_IMPL, DUPLICATE_EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Product name already exists",
              "Product Update Failed",
              HttpStatus.BAD_REQUEST,
              HttpStatusCodeEnum.BAD_REQUEST.getValue(),
              ErrorMessageCodeEnum.PRODUCT_UPDATE_FAILED.getCode(),
              new Date().toString(),
              null));

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to update product",
              "Product Update Failed",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.PRODUCT_UPDATE_FAILED.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Delete Product
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

      int deleted = productRepository.delete(correlationId, logKey, id);

      if (deleted == 0) {

        throw new GeneralStatusCodeErrorException(
            new ErrorMessage(
                "Product not found",
                "Product Delete Failed",
                HttpStatus.NOT_FOUND,
                HttpStatusCodeEnum.NOT_FOUND.getValue(),
                ErrorMessageCodeEnum.PRODUCT_NOT_FOUND.getCode(),
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
              "Failed to delete product",
              "Product Delete Failed",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.PRODUCT_DELETE_FAILED.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Find Product By Id
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return ProductDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public ProductDto findById(String correlationId, String logKey, long id)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      return productRepository
          .findById(correlationId, logKey, id)
          .orElseThrow(
              () ->
                  new GeneralStatusCodeErrorException(
                      new ErrorMessage(
                          "Product not found",
                          "Product Not Found",
                          HttpStatus.NOT_FOUND,
                          HttpStatusCodeEnum.NOT_FOUND.getValue(),
                          ErrorMessageCodeEnum.PRODUCT_NOT_FOUND.getCode(),
                          new Date().toString(),
                          null)));

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to find product",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Find All Products
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param search String
   * @param category String
   * @return PageResponse<ProductDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public PageResponse<ProductDto> findAll(
      String correlationId, String logKey, int page, int size, String search, String category)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      List<ProductDto> products =
          productRepository.findAll(correlationId, logKey, page, size, search, category);

      long total = productRepository.countAll(correlationId, logKey, search, category);

      return new PageResponse<>(products, page, size, total);

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to find products",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Get Low Stock Products
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ProductDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public List<ProductDto> getLowStockAlerts(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      return productRepository.getLowStockAlerts(correlationId, logKey);

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to get low stock products",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Get All Products
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ProductDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public List<ProductDto> getAllProducts(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      return productRepository.findAll(correlationId, logKey, 1, Integer.MAX_VALUE, "", null);

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to get products",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Get all low stock products across all users (no SecurityContext — for background jobs only).
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ProductDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public List<ProductDto> getAllLowStockProducts(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      return productRepository.getAllLowStockProducts(correlationId, logKey);

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to get all low stock products",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }
}
