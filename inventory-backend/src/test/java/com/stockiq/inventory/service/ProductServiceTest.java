package com.stockiq.inventory.service;

import com.stockiq.inventory.common.enums.product.ProductStatus;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.entity.Category;
import com.stockiq.inventory.entity.Product;
import com.stockiq.inventory.repository.ProductRepository;
import com.stockiq.inventory.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  private static final String CORRELATION_ID = "corr-id";
  private static final String LOG_KEY = "log-key";

  @Mock private ProductRepository productRepository;
  @Mock private CategoryService categoryService;

  @InjectMocks private ProductServiceImpl productService;

  private Product product;
  private ProductDto productDto;
  private Category category;

  @BeforeEach
  void setUp() {

    category = Category.builder().id(1L).name("Electronics").build();

    product =
        Product.builder()
            .id(1L)
            .name("Laptop")
            .categoryId(1L)
            .qty(10)
            .reorderPoint(2)
            .status(ProductStatus.IN_STOCK.getValue())
            .build();

    productDto =
        ProductDto.builder()
            .name("Laptop")
            .category("Electronics")
            .qty(10)
            .reorder(2)
            .status(ProductStatus.IN_STOCK)
            .build();
  }

  @Test
  @DisplayName("save product success")
  void save_ReturnsSuccess() throws Exception {
    // Arrange
    when(categoryService.findByName(anyString(), anyString(), anyString())).thenReturn(category);

    when(productRepository.save(anyString(), anyString(), any(Product.class))).thenReturn(1L);

    // Act
    ProductDto response = productService.save(CORRELATION_ID, LOG_KEY, productDto);

    // Assert
    assertNotNull(response);
    assertEquals(1L, response.getId());

    // Verify
    verify(categoryService, times(1)).findByName(anyString(), anyString(), anyString());

    verify(productRepository, times(1)).save(anyString(), anyString(), any(Product.class));
  }

  @Test
  @DisplayName("save product duplicate exception")
  void save_DuplicateException() throws Exception {
    // Arrange
    when(categoryService.findByName(anyString(), anyString(), anyString())).thenReturn(category);

    when(productRepository.save(anyString(), anyString(), any(Product.class)))
        .thenThrow(new DuplicateKeyException("duplicate"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productService.save(CORRELATION_ID, LOG_KEY, productDto));

    // Verify
    verify(productRepository, times(1)).save(anyString(), anyString(), any(Product.class));
  }

  @Test
  @DisplayName("save product generic exception")
  void save_GenericException() throws Exception {
    // Arrange
    when(categoryService.findByName(anyString(), anyString(), anyString())).thenReturn(category);

    when(productRepository.save(anyString(), anyString(), any(Product.class)))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productService.save(CORRELATION_ID, LOG_KEY, productDto));

    // Verify
    verify(productRepository, times(1)).save(anyString(), anyString(), any(Product.class));
  }

  @Test
  @DisplayName("save product with null status should default to IN_STOCK")
  void save_NullStatus_DefaultsToInStock() throws Exception {
    // Arrange
    ProductDto dto =
        ProductDto.builder()
            .name("Laptop")
            .category("Electronics")
            .qty(10)
            .reorder(2)
            .status(null)
            .build();

    when(categoryService.findByName(anyString(), anyString(), anyString())).thenReturn(category);

    when(productRepository.save(anyString(), anyString(), any(Product.class))).thenReturn(1L);

    // Act
    productService.save(CORRELATION_ID, LOG_KEY, dto);

    // Assert + Capture
    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

    verify(productRepository).save(anyString(), anyString(), productCaptor.capture());

    Product savedProduct = productCaptor.getValue();

    // This is the missing branch check
    assertEquals(ProductStatus.IN_STOCK.getValue(), savedProduct.getStatus());
  }

  @Test
  @DisplayName("find product by id not found")
  void findById_NotFound() throws Exception {
    // Arrange
    when(productRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productService.findById(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(productRepository, times(1)).findById(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("find product by id exception")
  void findById_Exception() throws Exception {
    // Arrange
    when(productRepository.findById(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productService.findById(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(productRepository, times(1)).findById(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("delete product success")
  void delete_ReturnsSuccess() throws Exception {
    // Arrange
    when(productRepository.delete(anyString(), anyString(), anyLong())).thenReturn(1);

    // Act
    assertDoesNotThrow(() -> productService.delete(CORRELATION_ID, LOG_KEY, 1L));

    // Assert

    // Verify
    verify(productRepository, times(1)).delete(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("delete product not found")
  void delete_NotFound() throws Exception {
    // Arrange
    when(productRepository.delete(anyString(), anyString(), anyLong())).thenReturn(0);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productService.delete(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(productRepository, times(1)).delete(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("delete product exception")
  void delete_Exception() throws Exception {
    // Arrange
    when(productRepository.delete(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productService.delete(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(productRepository, times(1)).delete(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("find all products success")
  void findAll_ReturnsSuccess() throws Exception {
    // Arrange
    when(productRepository.findAll(
            eq(CORRELATION_ID), eq(LOG_KEY), eq(0), eq(10), eq(""), eq(null)))
        .thenReturn(List.of(productDto));

    when(productRepository.countAll(eq(CORRELATION_ID), eq(LOG_KEY), eq(""), eq(null)))
        .thenReturn(1L);

    // Act
    PageResponse<ProductDto> response =
        productService.findAll(CORRELATION_ID, LOG_KEY, 0, 10, "", null);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getContent().size());

    // Verify
    verify(productRepository, times(1))
        .findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), any());

    verify(productRepository, times(1)).countAll(anyString(), anyString(), anyString(), any());
  }

  @Test
  @DisplayName("find all products exception")
  void findAll_Exception() throws Exception {
    // Arrange
    when(productRepository.findAll(
            anyString(), anyString(), anyInt(), anyInt(), anyString(), nullable(String.class)))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productService.findAll(CORRELATION_ID, LOG_KEY, 0, 10, "", null));

    // Verify
    verify(productRepository, times(1))
        .findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), nullable(String.class));
  }

  @Test
  @DisplayName("update product success")
  void update_Success() throws Exception {
    // Arrange
    when(categoryService.findByName(anyString(), anyString(), anyString())).thenReturn(category);

    when(productRepository.update(anyString(), anyString(), any(Product.class))).thenReturn(1);

    try (MockedStatic<com.stockiq.inventory.common.util.SecurityUtils> mockedSecurity =
        mockStatic(com.stockiq.inventory.common.util.SecurityUtils.class)) {

      mockedSecurity
          .when(com.stockiq.inventory.common.util.SecurityUtils::getCurrentUserId)
          .thenReturn("user-1");

      // Act
      assertDoesNotThrow(() -> productService.update(CORRELATION_ID, LOG_KEY, productDto, 1L));

      // Assert / Verify
      verify(categoryService, times(1)).findByName(anyString(), anyString(), anyString());

      verify(productRepository, times(1)).update(anyString(), anyString(), any(Product.class));
    }
  }

  @Test
  @DisplayName("update product not found")
  void update_NotFound() throws Exception {
    // Arrange
    when(categoryService.findByName(anyString(), anyString(), anyString())).thenReturn(category);

    when(productRepository.update(anyString(), anyString(), any(Product.class))).thenReturn(0);

    try (MockedStatic<com.stockiq.inventory.common.util.SecurityUtils> mockedSecurity =
        mockStatic(com.stockiq.inventory.common.util.SecurityUtils.class)) {

      mockedSecurity
          .when(com.stockiq.inventory.common.util.SecurityUtils::getCurrentUserId)
          .thenReturn("user-1");

      // Act & Assert
      assertThrows(
          GeneralStatusCodeErrorException.class,
          () -> productService.update(CORRELATION_ID, LOG_KEY, productDto, 1L));

      verify(productRepository, times(1)).update(anyString(), anyString(), any(Product.class));
    }
  }

  @Test
  @DisplayName("update product duplicate key exception")
  void update_DuplicateKeyException() throws Exception {
    // Arrange
    when(categoryService.findByName(anyString(), anyString(), anyString())).thenReturn(category);

    when(productRepository.update(anyString(), anyString(), any(Product.class)))
        .thenThrow(new DuplicateKeyException("duplicate"));

    try (MockedStatic<com.stockiq.inventory.common.util.SecurityUtils> mockedSecurity =
        mockStatic(com.stockiq.inventory.common.util.SecurityUtils.class)) {

      mockedSecurity
          .when(com.stockiq.inventory.common.util.SecurityUtils::getCurrentUserId)
          .thenReturn("user-1");

      // Act & Assert
      assertThrows(
          GeneralStatusCodeErrorException.class,
          () -> productService.update(CORRELATION_ID, LOG_KEY, productDto, 1L));
    }
  }

  @Test
  @DisplayName("update product generic exception")
  void update_GenericException() throws Exception {
    // Arrange
    when(categoryService.findByName(anyString(), anyString(), anyString())).thenReturn(category);

    when(productRepository.update(anyString(), anyString(), any(Product.class)))
        .thenThrow(new RuntimeException("DB error"));

    try (MockedStatic<com.stockiq.inventory.common.util.SecurityUtils> mockedSecurity =
        mockStatic(com.stockiq.inventory.common.util.SecurityUtils.class)) {

      mockedSecurity
          .when(com.stockiq.inventory.common.util.SecurityUtils::getCurrentUserId)
          .thenReturn("user-1");

      // Act & Assert
      assertThrows(
          GeneralStatusCodeErrorException.class,
          () -> productService.update(CORRELATION_ID, LOG_KEY, productDto, 1L));
    }
  }

  @Test
  @DisplayName("update product with null status should default to IN_STOCK")
  void update_NullStatus_DefaultsToInStock() throws Exception {
    // Arrange
    ProductDto dto =
        ProductDto.builder()
            .name("Laptop")
            .category("Electronics")
            .qty(10)
            .reorder(2)
            .status(null)
            .build();

    when(categoryService.findByName(anyString(), anyString(), anyString())).thenReturn(category);

    when(productRepository.update(anyString(), anyString(), any(Product.class))).thenReturn(1);

    try (MockedStatic<com.stockiq.inventory.common.util.SecurityUtils> mocked =
        mockStatic(com.stockiq.inventory.common.util.SecurityUtils.class)) {

      mocked
          .when(com.stockiq.inventory.common.util.SecurityUtils::getCurrentUserId)
          .thenReturn("user-1");

      // Act
      productService.update(CORRELATION_ID, LOG_KEY, dto, 1L);

      // Assert → capture saved object
      ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

      verify(productRepository).update(anyString(), anyString(), captor.capture());

      Product saved = captor.getValue();

      assertEquals(ProductStatus.IN_STOCK.getValue(), saved.getStatus());
    }
  }

  @Test
  @DisplayName("get low stock alerts success")
  void getLowStockAlerts_Success() throws Exception {
    // Arrange
    List<ProductDto> mockList = List.of(productDto);

    when(productRepository.getLowStockAlerts(anyString(), anyString())).thenReturn(mockList);

    // Act
    List<ProductDto> response = productService.getLowStockAlerts(CORRELATION_ID, LOG_KEY);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.size());

    // Verify
    verify(productRepository, times(1)).getLowStockAlerts(anyString(), anyString());
  }

  @Test
  @DisplayName("get low stock alerts exception")
  void getLowStockAlerts_Exception() throws Exception {
    // Arrange
    when(productRepository.getLowStockAlerts(anyString(), anyString()))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productService.getLowStockAlerts(CORRELATION_ID, LOG_KEY));

    // Verify
    verify(productRepository, times(1)).getLowStockAlerts(anyString(), anyString());
  }

  @Test
  @DisplayName("get all products success")
  void getAllProducts_Success() throws Exception {
    // Arrange
    List<ProductDto> mockList = List.of(productDto);

    when(productRepository.findAll(
            anyString(), anyString(), anyInt(), anyInt(), anyString(), nullable(String.class)))
        .thenReturn(mockList);

    // Act
    List<ProductDto> response = productService.getAllProducts(CORRELATION_ID, LOG_KEY);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.size());

    // Verify
    verify(productRepository, times(1))
        .findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), nullable(String.class));
  }

  @Test
  @DisplayName("get all products exception")
  void getAllProducts_Exception() throws Exception {
    // Arrange
    when(productRepository.findAll(
            anyString(), anyString(), anyInt(), anyInt(), anyString(), nullable(String.class)))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productService.getAllProducts(CORRELATION_ID, LOG_KEY));

    // Verify
    verify(productRepository, times(1))
        .findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), nullable(String.class));
  }

  @Test
  @DisplayName("get all low stock products success")
  void getAllLowStockProducts_Success() throws Exception {
    // Arrange
    List<ProductDto> mockList = List.of(productDto);

    when(productRepository.getAllLowStockProducts(anyString(), anyString())).thenReturn(mockList);

    // Act
    List<ProductDto> result = productService.getAllLowStockProducts(CORRELATION_ID, LOG_KEY);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());

    // Verify
    verify(productRepository, times(1)).getAllLowStockProducts(anyString(), anyString());
  }

  @Test
  @DisplayName("get all low stock products exception")
  void getAllLowStockProducts_Exception() throws Exception {
    // Arrange
    when(productRepository.getAllLowStockProducts(anyString(), anyString()))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> productService.getAllLowStockProducts(CORRELATION_ID, LOG_KEY));

    // Verify
    verify(productRepository, times(1)).getAllLowStockProducts(anyString(), anyString());
  }
}
