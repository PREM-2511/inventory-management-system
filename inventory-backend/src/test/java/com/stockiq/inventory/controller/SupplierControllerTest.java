package com.stockiq.inventory.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.supplier.SupplierStatus;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.supplier.CreateSupplierResponse;
import com.stockiq.inventory.common.response.supplier.DeleteSupplierResponse;
import com.stockiq.inventory.common.response.supplier.SupplierDetails;
import com.stockiq.inventory.common.response.supplier.UpdateSupplierResponse;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.SupplierDto;
import com.stockiq.inventory.service.SupplierService;
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
@DisplayName("SupplierController Test")
class SupplierControllerTest {

  private static final String X_CORRELATION_ID = "X-Correlation-Id";

  @Mock private SupplierService supplierService;

  @InjectMocks private SupplierController supplierController;

  private SupplierDto supplierDto;
  private SupplierDto savedSupplier;
  private PageResponse<SupplierDetails> pageResponse;

  @BeforeEach
  void setUp() {

    supplierDto =
        SupplierDto.builder()
            .id(1L)
            .name("ABC Suppliers")
            .category("Electronics")
            .email("abc@gmail.com")
            .phone("0771234567")
            .status(SupplierStatus.ACTIVE)
            .createdAt("2026-06-27 10:00:00")
            .build();

    savedSupplier =
        SupplierDto.builder()
            .id(1L)
            .name("ABC Suppliers")
            .category("Electronics")
            .email("abc@gmail.com")
            .phone("0771234567")
            .status(SupplierStatus.ACTIVE)
            .createdAt("2026-06-27 10:00:00")
            .build();
  }

  @Test
  @DisplayName("Create Supplier Success")
  void create_Success() throws Exception {
    // Arrange
    when(supplierService.save(anyString(), anyString(), any(SupplierDto.class)))
        .thenReturn(savedSupplier);

    // Act
    ResponseEntity<CreateSupplierResponse> response =
        supplierController.create(supplierDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.CREATE.getValue(), response.getStatusCode().value());

    CreateSupplierResponse body = response.getBody();
    assertNotNull(body);
    assertEquals(1L, body.getId());
    assertEquals("ABC Suppliers", body.getName());
    assertEquals("Electronics", body.getCategory());
    assertEquals("abc@gmail.com", body.getEmail());

    // Verify
    verify(supplierService).save(anyString(), anyString(), any(SupplierDto.class));
  }

  @Test
  @DisplayName("Create Supplier Exception")
  void create_Exception() throws GeneralStatusCodeErrorException {

    // Arrange
    when(supplierService.save(anyString(), anyString(), any(SupplierDto.class)))
        .thenThrow(RuntimeException.class);

    // Act & Assert
    assertThrows(
        RuntimeException.class, () -> supplierController.create(supplierDto, X_CORRELATION_ID));

    // Verify
    verify(supplierService).save(anyString(), anyString(), any(SupplierDto.class));
  }

  @Test
  @DisplayName("List Suppliers Success")
  void list_Success() throws Exception {
    // Arrange
    when(supplierService.findAll(anyString(), anyString(), anyInt(), anyInt(), anyString()))
        .thenReturn(pageResponse);

    // Act
    ResponseEntity<PageResponse<SupplierDetails>> response =
        supplierController.list(X_CORRELATION_ID, 1, 10, "");

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(pageResponse, response.getBody());

    // Verify
    verify(supplierService).findAll(anyString(), anyString(), anyInt(), anyInt(), anyString());
  }

  @Test
  @DisplayName("List Suppliers Exception")
  void list_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    when(supplierService.findAll(anyString(), anyString(), anyInt(), anyInt(), anyString()))
        .thenThrow(RuntimeException.class);

    // Act & Assert
    assertThrows(
        RuntimeException.class, () -> supplierController.list(X_CORRELATION_ID, 1, 10, ""));

    // Verify
    verify(supplierService).findAll(anyString(), anyString(), anyInt(), anyInt(), anyString());
  }

  @Test
  @DisplayName("Get Supplier By Id Success")
  void getById_Success() throws Exception {
    // Arrange
    SupplierDetails details =
        SupplierDetails.builder()
            .id(1L)
            .name("ABC Suppliers")
            .category("Electronics")
            .email("abc@gmail.com")
            .phone("0771234567")
            .status(SupplierStatus.ACTIVE)
            .build();

    when(supplierService.findById(anyString(), anyString(), anyLong())).thenReturn(details);

    // Act
    ResponseEntity<SupplierDetails> response = supplierController.getById(1L, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals(details, response.getBody());

    // Verify
    verify(supplierService).findById(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Get Supplier By Id Exception")
  void getById_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    when(supplierService.findById(anyString(), anyString(), anyLong()))
        .thenThrow(RuntimeException.class);

    // Act & Assert
    assertThrows(RuntimeException.class, () -> supplierController.getById(1L, X_CORRELATION_ID));

    // Verify
    verify(supplierService).findById(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Update Supplier Success")
  void update_Success() throws Exception {
    // Arrange
    doNothing()
        .when(supplierService)
        .update(anyString(), anyString(), any(SupplierDto.class), anyLong());

    // Act
    ResponseEntity<UpdateSupplierResponse> response =
        supplierController.update(1L, supplierDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals("Supplier updated successfully", response.getBody().getMessage());

    // Verify
    verify(supplierService).update(anyString(), anyString(), any(SupplierDto.class), eq(1L));
  }

  @Test
  @DisplayName("Update Supplier Exception")
  void update_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    doThrow(RuntimeException.class)
        .when(supplierService)
        .update(anyString(), anyString(), any(SupplierDto.class), anyLong());

    // Act & Assert
    assertThrows(
        RuntimeException.class, () -> supplierController.update(1L, supplierDto, X_CORRELATION_ID));

    // Verify
    verify(supplierService).update(anyString(), anyString(), any(SupplierDto.class), eq(1L));
  }

  @Test
  @DisplayName("Delete Supplier Success")
  void delete_Success() throws Exception {
    // Arrange
    doNothing().when(supplierService).delete(anyString(), anyString(), anyLong());

    // Act
    ResponseEntity<DeleteSupplierResponse> response =
        supplierController.delete(1L, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());
    assertEquals("Supplier deleted successfully", response.getBody().getMessage());

    // Verify
    verify(supplierService).delete(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Delete Supplier Exception")
  void delete_Exception() throws GeneralStatusCodeErrorException {
    // Arrange
    doThrow(RuntimeException.class)
        .when(supplierService)
        .delete(anyString(), anyString(), anyLong());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> supplierController.delete(1L, X_CORRELATION_ID));

    // Verify
    verify(supplierService).delete(anyString(), anyString(), eq(1L));
  }

  @Test
  @DisplayName("Get Supplier Statuses Success")
  void getStatuses_Success() {
    // Act
    ResponseEntity<List<java.util.Map<String, Object>>> response = supplierController.getStatuses();

    // Assert
    assertEquals(4, response.getBody().size());
    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());

    // Verify no service call
    verifyNoInteractions(supplierService);
  }
}
