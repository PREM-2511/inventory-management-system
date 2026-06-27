package com.stockiq.inventory.service;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.supplier.SupplierDetails;
import com.stockiq.inventory.dto.SupplierDto;
import com.stockiq.inventory.entity.Supplier;
import com.stockiq.inventory.repository.SupplierRepository;
import com.stockiq.inventory.service.impl.SupplierServiceImpl;
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
class SupplierServiceTest {

  private static final String CORRELATION_ID = "corr-id";
  private static final String LOG_KEY = "log-key";

  @Mock private SupplierRepository supplierRepository;

  @InjectMocks private SupplierServiceImpl supplierService;

  private Supplier supplier;
  private SupplierDto supplierDto;

  @BeforeEach
  void setUp() {

    supplier =
        Supplier.builder()
            .id(1L)
            .name("ABC Suppliers")
            .category("Electronics")
            .status("ACTIVE")
            .email("abc@test.com")
            .phone("123456789")
            .build();

    supplierDto =
        SupplierDto.builder()
            .name("ABC Suppliers")
            .category("Electronics")
            .email("abc@test.com")
            .phone("123456789")
            .build();
  }

  @Test
  @DisplayName("save supplier success")
  void save_Success() throws Exception {
    // Arrange
    when(supplierRepository.save(anyString(), anyString(), any(Supplier.class))).thenReturn(1L);

    // Act
    SupplierDto response = supplierService.save(CORRELATION_ID, LOG_KEY, supplierDto);

    // Assert
    assertNotNull(response);
    assertEquals(1L, response.getId());

    // Verify
    verify(supplierRepository, times(1)).save(anyString(), anyString(), any(Supplier.class));
  }

  @Test
  @DisplayName("save supplier duplicate exception")
  void save_DuplicateException() throws Exception {
    // Arrange
    when(supplierRepository.save(anyString(), anyString(), any(Supplier.class)))
        .thenThrow(new DuplicateKeyException("duplicate"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> supplierService.save(CORRELATION_ID, LOG_KEY, supplierDto));

    // Verify
    verify(supplierRepository, times(1)).save(anyString(), anyString(), any(Supplier.class));
  }

  @Test
  @DisplayName("save supplier generic exception")
  void save_GenericException() throws Exception {
    // Arrange
    when(supplierRepository.save(anyString(), anyString(), any(Supplier.class)))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> supplierService.save(CORRELATION_ID, LOG_KEY, supplierDto));

    // Verify
    verify(supplierRepository, times(1)).save(anyString(), anyString(), any(Supplier.class));
  }

  @Test
  @DisplayName("update supplier success")
  void update_Success() throws Exception {
    // Arrange
    when(supplierRepository.update(anyString(), anyString(), any(Supplier.class))).thenReturn(1);

    // Act
    assertDoesNotThrow(() -> supplierService.update(CORRELATION_ID, LOG_KEY, supplierDto, 1L));

    // Verify
    verify(supplierRepository, times(1)).update(anyString(), anyString(), any(Supplier.class));
  }

  @Test
  @DisplayName("update supplier not found")
  void update_NotFound() throws Exception {
    // Arrange
    when(supplierRepository.update(anyString(), anyString(), any(Supplier.class))).thenReturn(0);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> supplierService.update(CORRELATION_ID, LOG_KEY, supplierDto, 1L));

    // Verify
    verify(supplierRepository, times(1)).update(anyString(), anyString(), any(Supplier.class));
  }

  @Test
  @DisplayName("update supplier duplicate exception")
  void update_DuplicateException() throws Exception {
    // Arrange
    when(supplierRepository.update(anyString(), anyString(), any(Supplier.class)))
        .thenThrow(new DuplicateKeyException("duplicate"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> supplierService.update(CORRELATION_ID, LOG_KEY, supplierDto, 1L));

    // Verify
    verify(supplierRepository, times(1)).update(anyString(), anyString(), any(Supplier.class));
  }

  @Test
  @DisplayName("update supplier generic exception")
  void update_GenericException() throws Exception {
    // Arrange
    when(supplierRepository.update(anyString(), anyString(), any(Supplier.class)))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> supplierService.update(CORRELATION_ID, LOG_KEY, supplierDto, 1L));

    // Verify
    verify(supplierRepository, times(1)).update(anyString(), anyString(), any(Supplier.class));
  }

  @Test
  @DisplayName("delete supplier success")
  void delete_Success() throws Exception {
    // Arrange
    when(supplierRepository.delete(anyString(), anyString(), anyLong())).thenReturn(1);

    // Act
    assertDoesNotThrow(() -> supplierService.delete(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(supplierRepository, times(1)).delete(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("delete supplier not found")
  void delete_NotFound() throws Exception {
    // Arrange
    when(supplierRepository.delete(anyString(), anyString(), anyLong())).thenReturn(0);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> supplierService.delete(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(supplierRepository, times(1)).delete(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("delete supplier exception")
  void delete_Exception() throws Exception {
    // Arrange
    when(supplierRepository.delete(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> supplierService.delete(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(supplierRepository, times(1)).delete(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("find supplier by id success")
  void findById_Success() throws Exception {
    // Arrange
    when(supplierRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.of(supplier));

    // Act
    SupplierDetails response = supplierService.findById(CORRELATION_ID, LOG_KEY, 1L);

    // Assert
    assertNotNull(response);
    assertEquals(1L, response.getId());

    // Verify
    verify(supplierRepository, times(1)).findById(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("find supplier by id not found")
  void findById_NotFound() throws Exception {
    // Arrange
    when(supplierRepository.findById(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> supplierService.findById(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(supplierRepository, times(1)).findById(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("find supplier by id exception")
  void findById_Exception() throws Exception {
    // Arrange
    when(supplierRepository.findById(anyString(), anyString(), anyLong()))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> supplierService.findById(CORRELATION_ID, LOG_KEY, 1L));

    // Verify
    verify(supplierRepository, times(1)).findById(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("find all suppliers success")
  void findAll_Success() throws Exception {
    // Arrange
    when(supplierRepository.findAll(anyString(), anyString(), anyInt(), anyInt(), anyString()))
        .thenReturn(List.of(supplier));

    when(supplierRepository.countAll(anyString(), anyString(), anyString())).thenReturn(1L);

    // Act
    var response = supplierService.findAll(CORRELATION_ID, LOG_KEY, 0, 10, "");

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getContent().size());

    // Verify
    verify(supplierRepository, times(1))
        .findAll(anyString(), anyString(), anyInt(), anyInt(), anyString());

    verify(supplierRepository, times(1)).countAll(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("find all suppliers exception")
  void findAll_Exception() throws Exception {
    // Arrange
    when(supplierRepository.findAll(anyString(), anyString(), anyInt(), anyInt(), anyString()))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> supplierService.findAll(CORRELATION_ID, LOG_KEY, 0, 10, ""));

    // Verify
    verify(supplierRepository, times(1))
        .findAll(anyString(), anyString(), anyInt(), anyInt(), anyString());
  }
}
