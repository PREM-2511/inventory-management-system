package com.stockiq.inventory.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.stockiq.inventory.common.enums.ErrorMessageCodeEnum;
import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.category.Status;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.category.CategoryDetails;
import com.stockiq.inventory.common.response.category.CreateCategoryResponse;
import com.stockiq.inventory.common.response.category.DeleteCategoryResponse;
import com.stockiq.inventory.common.response.category.UpdateCategoryResponse;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.CategoryDto;
import com.stockiq.inventory.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryController Test")
class CategoryControllerTest {

  private static final String X_CORRELATION_ID = "X-Correlation-Id";

  @Mock private CategoryService categoryService;

  @InjectMocks private CategoryController categoryController;

  private CategoryDto categoryDto;
  private CategoryDto savedCategory;
  private PageResponse pageResponse;

  @BeforeEach
  void setUp() {

    categoryDto = new CategoryDto();
    categoryDto.setId(1L);
    categoryDto.setName("Electronics");
    categoryDto.setIcon("icon.png");
    categoryDto.setStatus(Status.ACTIVE);

    savedCategory = new CategoryDto();
    savedCategory.setId(1L);
    savedCategory.setName("Electronics");
    savedCategory.setIcon("icon.png");
    savedCategory.setStatus(Status.ACTIVE);
  }

  @Test
  @DisplayName("create Category Success")
  void create_Success() throws Exception {
    // Arrange
    when(categoryService.save(anyString(), anyString(), any(CategoryDto.class)))
        .thenReturn(savedCategory);

    // Act
    ResponseEntity<?> response = categoryController.create(categoryDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.CREATE.getValue(), response.getStatusCode().value());

    CreateCategoryResponse body = (CreateCategoryResponse) response.getBody();

    assertNotNull(body);
    assertEquals(1L, body.getId());
    assertEquals("Electronics", body.getName());
    assertEquals("icon.png", body.getIcon());
    assertEquals(Status.ACTIVE, body.getStatus());

    // Verify
    verify(categoryService).save(anyString(), anyString(), any(CategoryDto.class));
  }

  @Test
  @DisplayName("create Category Throws Exception")
  void create_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Category creation failed",
            "Create Category Failed",
            HttpStatus.BAD_REQUEST,
            HttpStatusCodeEnum.BAD_REQUEST.getValue(),
            ErrorMessageCodeEnum.CATEGORY_CREATE_FAILED.getCode(),
            new java.util.Date().toString(),
            null);

    when(categoryService.save(anyString(), anyString(), any(CategoryDto.class)))
        .thenThrow(new GeneralStatusCodeErrorException(errorMessage));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryController.create(categoryDto, X_CORRELATION_ID));

    // Verify
    verify(categoryService).save(anyString(), anyString(), any(CategoryDto.class));
  }

  @Test
  @DisplayName("list Categories Success")
  void list_Success() throws Exception {
    // Arrange
    when(categoryService.findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), any()))
        .thenReturn(pageResponse);

    // Act
    ResponseEntity<PageResponse<CategoryDetails>> response =
        categoryController.list(X_CORRELATION_ID, 1, 10, "", Status.ACTIVE);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(pageResponse, response.getBody());

    // Verify
    verify(categoryService)
        .findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), any());
  }

  @Test
  @DisplayName("list Categories Throws Exception")
  void list_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Find categories failed",
            "Find Failed",
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
            "CATEGORY_LIST_ERROR",
            new java.util.Date().toString(),
            null);

    when(categoryService.findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), any()))
        .thenThrow(new GeneralStatusCodeErrorException(errorMessage));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryController.list(X_CORRELATION_ID, 1, 10, "", Status.ACTIVE));

    // Verify
    verify(categoryService)
        .findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), any());
  }

  @Test
  @DisplayName("getById Success")
  void getById_Success() throws Exception {
    // Arrange
    when(categoryService.findById(anyString(), anyString(), anyLong())).thenReturn(savedCategory);

    // Act
    ResponseEntity<?> response = categoryController.getById(1L, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(savedCategory, response.getBody());

    // Verify
    verify(categoryService).findById(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("getById Throws Exception")
  void getById_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Internal Server Error",
            "Test Exception",
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
            ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
            new java.util.Date().toString(),
            null);

    when(categoryService.findById(anyString(), anyString(), anyLong()))
        .thenThrow(new GeneralStatusCodeErrorException(errorMessage));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryController.getById(1L, X_CORRELATION_ID));

    // Verify
    verify(categoryService).findById(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("update Category Success")
  void update_Success() throws Exception {
    // Arrange
    doNothing()
        .when(categoryService)
        .update(anyString(), anyString(), any(CategoryDto.class), anyLong());

    // Act
    ResponseEntity<?> response = categoryController.update(1L, categoryDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());

    UpdateCategoryResponse body = (UpdateCategoryResponse) response.getBody();
    assertNotNull(body);
    assertEquals("Category updated successfully", body.getMessage());

    // Verify
    verify(categoryService).update(anyString(), anyString(), any(CategoryDto.class), eq(1L));
  }

  @Test
  @DisplayName("update Category Throws Exception")
  void update_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Update failed",
            "Category update failed",
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
            ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
            new java.util.Date().toString(),
            null);

    doThrow(new GeneralStatusCodeErrorException(errorMessage))
        .when(categoryService)
        .update(anyString(), anyString(), any(CategoryDto.class), anyLong());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryController.update(1L, categoryDto, X_CORRELATION_ID));

    // Verify
    verify(categoryService).update(anyString(), anyString(), any(CategoryDto.class), eq(1L));
  }

  @Test
  @DisplayName("delete Category Success")
  void delete_Success() throws Exception {
    // Arrange
    doNothing().when(categoryService).delete(anyString(), anyString(), anyLong());

    // Act
    ResponseEntity<?> response = categoryController.delete(1L, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());

    DeleteCategoryResponse body = (DeleteCategoryResponse) response.getBody();
    assertNotNull(body);
    assertEquals("Category deleted successfully", body.getMessage());

    // Verify
    verify(categoryService).delete(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("delete Category Throws Exception")
  void delete_ServiceThrowsException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Delete failed",
            "Category delete failed",
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
            ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
            new java.util.Date().toString(),
            null);

    doThrow(new GeneralStatusCodeErrorException(errorMessage))
        .when(categoryService)
        .delete(anyString(), anyString(), anyLong());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryController.delete(1L, X_CORRELATION_ID));

    // Verify
    verify(categoryService).delete(anyString(), anyString(), eq(1L));
  }
}
