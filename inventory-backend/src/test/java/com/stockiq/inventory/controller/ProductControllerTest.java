package com.stockiq.inventory.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.product.CreateProductResponse;
import com.stockiq.inventory.common.response.product.DeleteProductResponse;
import com.stockiq.inventory.common.response.product.UpdateProductResponse;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController Test")
class ProductControllerTest {

  private static final String X_CORRELATION_ID = "X-Correlation-Id";

  @Mock private ProductService productService;

  @InjectMocks private ProductController productController;

  private ProductDto productDto;
  private ProductDto savedProduct;
  private PageResponse<ProductDto> pageResponse;

  @BeforeEach
  void setUp() {

    productDto =
        ProductDto.builder()
            .id(1L)
            .name("Laptop")
            .category("Electronics")
            .qty(10)
            .reorder(2)
            .createdAt("2026-06-27 10:00:00")
            .build();

    savedProduct =
        ProductDto.builder()
            .id(1L)
            .name("Laptop")
            .category("Electronics")
            .qty(10)
            .reorder(2)
            .createdAt("2026-06-27 10:00:00")
            .build();
  }

  @Test
  @DisplayName("Create Product Success")
  void create_Success() throws Exception {
    // Arrange
    when(productService.save(anyString(), anyString(), any(ProductDto.class)))
        .thenReturn(savedProduct);

    // Act
    ResponseEntity<CreateProductResponse> response =
        productController.create(productDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.CREATE.getValue(), response.getStatusCode().value());

    CreateProductResponse body = response.getBody();
    assertNotNull(body);
    assertEquals(1L, body.getId());
    assertEquals("Laptop", body.getName());

    // Verify
    verify(productService).save(anyString(), anyString(), any(ProductDto.class));
  }

  @Test
  @DisplayName("Create Product Exception")
  void create_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    when(productService.save(anyString(), anyString(), any(ProductDto.class)))
        .thenThrow(GeneralStatusCodeErrorException.class);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productController.create(productDto, X_CORRELATION_ID));

    // Verify
    verify(productService).save(anyString(), anyString(), any(ProductDto.class));
  }

  @Test
  @DisplayName("List Products Success")
  void list_Success() throws Exception {
    // Arrange
    when(productService.findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), any()))
        .thenReturn(pageResponse);

    // Act
    ResponseEntity<PageResponse<ProductDto>> response =
        productController.list(X_CORRELATION_ID, 1, 10, "", null);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(pageResponse, response.getBody());

    // Verify
    verify(productService)
        .findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), any());
  }

  @Test
  @DisplayName("List Products Exception")
  void list_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    when(productService.findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), any()))
        .thenThrow(GeneralStatusCodeErrorException.class);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productController.list(X_CORRELATION_ID, 1, 10, "", null));

    // Verify
    verify(productService)
        .findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), any());
  }

  @Test
  @DisplayName("Get Product By Id Success")
  void getById_Success() throws Exception {
    // Arrange
    when(productService.findById(anyString(), anyString(), anyLong())).thenReturn(savedProduct);

    // Act
    ResponseEntity<ProductDto> response = productController.getById(1L, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(savedProduct, response.getBody());

    // Verify
    verify(productService).findById(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Get By Id Exception")
  void getById_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    when(productService.findById(anyString(), anyString(), anyLong()))
        .thenThrow(GeneralStatusCodeErrorException.class);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productController.getById(1L, X_CORRELATION_ID));

    // Verify
    verify(productService).findById(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Update Product Success")
  void update_Success() throws Exception {
    // Arrange
    doNothing()
        .when(productService)
        .update(anyString(), anyString(), any(ProductDto.class), anyLong());

    // Act
    ResponseEntity<UpdateProductResponse> response =
        productController.update(1L, productDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals("Product updated successfully", response.getBody().getMessage());

    // Verify
    verify(productService).update(anyString(), anyString(), any(ProductDto.class), eq(1L));
  }

  @Test
  @DisplayName("Update Product Exception")
  void update_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    doThrow(GeneralStatusCodeErrorException.class)
        .when(productService)
        .update(anyString(), anyString(), any(ProductDto.class), anyLong());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productController.update(1L, productDto, X_CORRELATION_ID));

    // Verify
    verify(productService).update(anyString(), anyString(), any(ProductDto.class), eq(1L));
  }

  @Test
  @DisplayName("Delete Product Success")
  void delete_Success() throws Exception {
    // Arrange
    doNothing().when(productService).delete(anyString(), anyString(), anyLong());

    // Act
    ResponseEntity<DeleteProductResponse> response = productController.delete(1L, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals("Product deleted successfully", response.getBody().getMessage());

    // Verify
    verify(productService).delete(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Delete Product Exception")
  void delete_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    doThrow(GeneralStatusCodeErrorException.class)
        .when(productService)
        .delete(anyString(), anyString(), anyLong());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productController.delete(1L, X_CORRELATION_ID));

    // Verify
    verify(productService).delete(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Low Stock Success")
  void lowStock_Success() throws Exception {
    // Arrange
    when(productService.getLowStockAlerts(anyString(), anyString()))
        .thenReturn(List.of(productDto));

    // Act
    ResponseEntity<List<ProductDto>> response =
        productController.getLowStockProducts(X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(1, response.getBody().size());

    // Verify
    verify(productService).getLowStockAlerts(anyString(), anyString());
  }

  @Test
  @DisplayName("Low Stock Exception")
  void lowStock_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    when(productService.getLowStockAlerts(anyString(), anyString()))
        .thenThrow(GeneralStatusCodeErrorException.class);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productController.getLowStockProducts(X_CORRELATION_ID));

    // Verify
    verify(productService).getLowStockAlerts(anyString(), anyString());
  }
}
