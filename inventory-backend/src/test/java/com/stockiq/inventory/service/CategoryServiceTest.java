package com.stockiq.inventory.service;

import com.stockiq.inventory.common.enums.category.Status;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.category.CategoryDetails;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.CategoryDto;
import com.stockiq.inventory.entity.Category;
import com.stockiq.inventory.repository.CategoryRepository;
import com.stockiq.inventory.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  private static final String CORRELATION_ID = "corr-id";
  private static final String LOG_KEY = "log-key";

  @Mock private CategoryRepository categoryRepository;

  @InjectMocks private CategoryServiceImpl categoryService;

  private CategoryDto categoryDto;
  private Category category;

  @BeforeEach
  void setUp() {

    categoryDto = new CategoryDto();
    categoryDto.setName("Electronics");
    categoryDto.setIcon("icon.png");
    categoryDto.setStatus(Status.ACTIVE);

    category =
        Category.builder()
            .id(1L)
            .name("Electronics")
            .icon("icon.png")
            .status(Status.ACTIVE.getValue())
            .build();
  }

  @Test
  @DisplayName("save category success")
  void save_Success() throws Exception {
    // Arrange
    when(categoryRepository.save(anyString(), anyString(), any(Category.class))).thenReturn(1L);

    // Act
    CategoryDto result = categoryService.save(CORRELATION_ID, LOG_KEY, categoryDto);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());

    // Verify
    verify(categoryRepository, times(1)).save(anyString(), anyString(), any(Category.class));
  }

  @Test
  @DisplayName("save duplicate category throws exception")
  void save_Duplicate() throws Exception {
    // Arrange
    when(categoryRepository.save(anyString(), anyString(), any(Category.class)))
        .thenThrow(new DuplicateKeyException("duplicate"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryService.save(CORRELATION_ID, LOG_KEY, categoryDto));
  }

  @Test
  @DisplayName("save generic exception")
  void save_GenericException() throws Exception {
    // Arrange
    when(categoryRepository.save(anyString(), anyString(), any(Category.class)))
        .thenThrow(new RuntimeException("db error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryService.save(CORRELATION_ID, LOG_KEY, categoryDto));
  }

  @Test
  @DisplayName("save category uses ACTIVE status when dto status is null")
  void save_UsesDefaultActiveStatus_WhenStatusIsNull() throws Exception {
    // Arrange
    categoryDto.setStatus(null);

    when(categoryRepository.save(anyString(), anyString(), any(Category.class))).thenReturn(1L);

    // Act
    CategoryDto result = categoryService.save(CORRELATION_ID, LOG_KEY, categoryDto);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(Status.ACTIVE, result.getStatus());

    // Verify
    verify(categoryRepository, times(1)).save(anyString(), anyString(), any(Category.class));
  }

  @Test
  @DisplayName("update category success")
  void update_Success() throws Exception {
    // Arrange
    when(categoryRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.of(categoryDto));

    when(categoryRepository.update(anyString(), anyString(), any(Category.class))).thenReturn(1);

    // Act
    assertDoesNotThrow(() -> categoryService.update(CORRELATION_ID, LOG_KEY, categoryDto, 1L));

    // Verify
    verify(categoryRepository, times(1)).update(anyString(), anyString(), any(Category.class));
  }

  @Test
  @DisplayName("update category not found")
  void update_NotFound() throws Exception {
    // Arrange
    when(categoryRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.of(categoryDto));

    when(categoryRepository.update(anyString(), anyString(), any(Category.class))).thenReturn(0);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryService.update(CORRELATION_ID, LOG_KEY, categoryDto, 1L));
  }

  @Test
  @DisplayName("update duplicate category")
  void update_Duplicate() throws Exception {
    // Arrange
    when(categoryRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.of(categoryDto));

    when(categoryRepository.update(anyString(), anyString(), any(Category.class)))
        .thenThrow(new DuplicateKeyException("duplicate"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryService.update(CORRELATION_ID, LOG_KEY, categoryDto, 1L));
  }

  @Test
  @DisplayName("update generic exception")
  void update_GenericException() throws Exception {
    // Arrange
    when(categoryRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.of(categoryDto));

    when(categoryRepository.update(anyString(), anyString(), any(Category.class)))
        .thenThrow(new RuntimeException("db error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryService.update(CORRELATION_ID, LOG_KEY, categoryDto, 1L));
  }

  @Test
  @DisplayName("update uses existing category status when dto status is null")
  void update_UsesExistingStatus_WhenDtoStatusIsNull() throws Exception {
    // Arrange
    categoryDto.setStatus(null);

    CategoryDto existingCategory = new CategoryDto();
    existingCategory.setId(1L);
    existingCategory.setName("Electronics");
    existingCategory.setIcon("icon.png");
    existingCategory.setStatus(Status.INACTIVE);

    when(categoryRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.of(existingCategory));

    when(categoryRepository.update(anyString(), anyString(), any(Category.class))).thenReturn(1);

    // Act
    assertDoesNotThrow(() -> categoryService.update(CORRELATION_ID, LOG_KEY, categoryDto, 1L));

    // Verify
    verify(categoryRepository, times(1)).findById(anyString(), anyString(), anyLong());

    verify(categoryRepository, times(1)).update(anyString(), anyString(), any(Category.class));
  }

  @Test
  @DisplayName("delete success")
  void delete_Success() throws Exception {
    // Arrange
    when(categoryRepository.delete(anyString(), anyString(), anyLong())).thenReturn(1);

    // Act
    assertDoesNotThrow(() -> categoryService.delete(CORRELATION_ID, LOG_KEY, 1L));
  }

  @Test
  @DisplayName("delete not found")
  void delete_NotFound() throws Exception {
    // Arrange
    when(categoryRepository.delete(anyString(), anyString(), anyLong())).thenReturn(0);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryService.delete(CORRELATION_ID, LOG_KEY, 1L));
  }

  @Test
  @DisplayName("delete generic exception")
  void delete_GenericException() throws Exception {
    // Arrange
    when(categoryRepository.delete(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("db error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryService.delete(CORRELATION_ID, LOG_KEY, 1L));
  }

  @Test
  @DisplayName("find by id success")
  void findById_Success() throws Exception {
    // Arrange
    when(categoryRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.of(categoryDto));

    // Act
    CategoryDto result = categoryService.findById(CORRELATION_ID, LOG_KEY, 1L);

    // Assert
    assertNotNull(result);
  }

  @Test
  @DisplayName("find by id not found")
  void findById_NotFound() throws Exception {
    // Arrange
    when(categoryRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryService.findById(CORRELATION_ID, LOG_KEY, 1L));
  }

  @Test
  @DisplayName("find by id generic exception")
  void findById_GenericException() throws Exception {
    // Arrange
    when(categoryRepository.findById(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("db error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryService.findById(CORRELATION_ID, LOG_KEY, 1L));
  }

  @Test
  @DisplayName("find all success")
  void findAll_Success() throws Exception {
    // Arrange
    Category entity =
        Category.builder()
            .id(1L)
            .name("Electronics")
            .icon("icon.png")
            .status(Status.ACTIVE.getValue())
            .build();

    when(categoryRepository.findAll(
            anyString(), anyString(), anyInt(), anyInt(), anyString(), any()))
        .thenReturn(List.of(entity));

    when(categoryRepository.countAll(anyString(), anyString(), anyString(), any())).thenReturn(1L);

    // Act
    PageResponse<CategoryDetails> response =
        categoryService.findAll(CORRELATION_ID, LOG_KEY, 0, 10, "", Status.ACTIVE);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getContent().size());
  }

  @Test
  @DisplayName("find all exception")
  void findAll_Exception() throws Exception {
    // Arrange
    when(categoryRepository.findAll(
            anyString(), anyString(), anyInt(), anyInt(), anyString(), any()))
        .thenThrow(new RuntimeException("db error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryService.findAll(CORRELATION_ID, LOG_KEY, 0, 10, "", Status.ACTIVE));
  }

  @Test
  @DisplayName("find by name success")
  void findByName_Success() throws Exception {
    // Arrange
    when(categoryRepository.findByName(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(category));

    // Act
    Category result = categoryService.findByName(CORRELATION_ID, LOG_KEY, "Electronics");

    // Assert
    assertNotNull(result);
  }

  @Test
  @DisplayName("find by name not found")
  void findByName_NotFound() throws Exception {
    // Arrange
    when(categoryRepository.findByName(anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryService.findByName(CORRELATION_ID, LOG_KEY, "Electronics"));
  }

  @Test
  @DisplayName("find by name exception")
  void findByName_Exception() throws Exception {
    // Arrange
    when(categoryRepository.findByName(anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("db error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> categoryService.findByName(CORRELATION_ID, LOG_KEY, "Electronics"));
  }
}
