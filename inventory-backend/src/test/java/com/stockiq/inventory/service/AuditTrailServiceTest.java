package com.stockiq.inventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.stockiq.inventory.common.enums.audit.AuditAction;
import com.stockiq.inventory.common.enums.audit.AuditEntityType;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.AuditTrailDto;
import com.stockiq.inventory.entity.AuditTrail;
import com.stockiq.inventory.repository.AuditTrailRepository;
import com.stockiq.inventory.service.impl.AuditTrailServiceImpl;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditTrailService Test")
class AuditTrailServiceTest {

  private static final String CORRELATION_ID = "corr-id";
  private static final String LOG_KEY = "log-key";

  @Mock private AuditTrailRepository auditTrailRepository;

  @InjectMocks private AuditTrailServiceImpl auditTrailService;

  private AuditTrail auditTrail;

  @BeforeEach
  void setUp() {

    auditTrail =
        AuditTrail.builder()
            .id(1L)
            .userId(1L)
            .userEmail("test@test.com")
            .action(AuditAction.CREATE.getValue())
            .entityType(AuditEntityType.CATEGORY.getValue())
            .entityId(10L)
            .entityName("CATEGORY")
            .oldValue("OLD")
            .newValue("NEW")
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .build();
  }

  @Test
  @DisplayName("record success")
  void record_Success() throws Exception {

    // Arrange
    when(auditTrailRepository.save(anyString(), anyString(), any(AuditTrail.class))).thenReturn(1L);

    // Act
    auditTrailService.record(
        CORRELATION_ID,
        LOG_KEY,
        AuditAction.CREATE,
        AuditEntityType.CATEGORY,
        10L,
        "CATEGORY",
        null,
        "NEW");

    // Assert + Verify
    verify(auditTrailRepository, times(1)).save(anyString(), anyString(), any(AuditTrail.class));
  }

  @Test
  @DisplayName("getAuditTrail success")
  void getAuditTrail_Success() throws Exception {

    // Arrange
    when(auditTrailRepository.findAll(
            anyString(), anyString(), anyInt(), anyInt(), any(), any(), any()))
        .thenReturn(List.of(auditTrail));

    when(auditTrailRepository.countAll(anyString(), anyString(), any(), any(), any()))
        .thenReturn(1L);

    // Act
    PageResponse<AuditTrailDto> result =
        auditTrailService.getAuditTrail(
            CORRELATION_ID, LOG_KEY, 1, 10, "1", "2026-01-01", "2026-01-02");

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(1L, result.getTotalElements());

    // Verify
    verify(auditTrailRepository, times(1))
        .findAll(anyString(), anyString(), anyInt(), anyInt(), any(), any(), any());

    verify(auditTrailRepository, times(1)).countAll(anyString(), anyString(), any(), any(), any());
  }

  @Test
  @DisplayName("getAuditTrail no filters")
  void getAuditTrail_NoFilters() throws Exception {

    // Arrange
    when(auditTrailRepository.findAll(
            anyString(), anyString(), anyInt(), anyInt(), any(), any(), any()))
        .thenReturn(List.of(auditTrail));

    when(auditTrailRepository.countAll(anyString(), anyString(), any(), any(), any()))
        .thenReturn(1L);

    // Act
    PageResponse<AuditTrailDto> result =
        auditTrailService.getAuditTrail(CORRELATION_ID, LOG_KEY, 0, 10, null, null, null);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
  }

  @Test
  @DisplayName("record repository throws exception but handled")
  void record_ExceptionHandled() throws Exception {

    // Arrange
    when(auditTrailRepository.save(anyString(), anyString(), any(AuditTrail.class)))
        .thenThrow(new RuntimeException("DB error"));

    // Act (should NOT throw)
    assertDoesNotThrow(
        () ->
            auditTrailService.record(
                CORRELATION_ID,
                LOG_KEY,
                AuditAction.CREATE,
                AuditEntityType.CATEGORY,
                10L,
                "CATEGORY",
                null,
                "NEW"));

    // Verify still called
    verify(auditTrailRepository, times(1)).save(anyString(), anyString(), any(AuditTrail.class));
  }

  @Test
  @DisplayName("getAuditTrail throws exception")
  void getAuditTrail_Exception() throws Exception {

    // Arrange
    when(auditTrailRepository.findAll(
            anyString(), anyString(), anyInt(), anyInt(), any(), any(), any()))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(
        RuntimeException.class,
        () -> auditTrailService.getAuditTrail(CORRELATION_ID, LOG_KEY, 1, 10, null, null, null));

    verify(auditTrailRepository, times(1))
        .findAll(anyString(), anyString(), anyInt(), anyInt(), any(), any(), any());
  }
}
