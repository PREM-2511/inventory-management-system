package com.stockiq.inventory.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.AuditTrailDto;
import com.stockiq.inventory.service.AuditTrailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditTrailController Test")
class AuditTrailControllerTest {

  private static final String CORRELATION_ID = "X-Correlation-Id";

  @Mock private AuditTrailService auditTrailService;

  @InjectMocks private AuditTrailController auditTrailController;

  private PageResponse<AuditTrailDto> pageResponse;

  @BeforeEach
  void setUp() {

    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("list Success Returns PageResponse")
  void list_Success_ReturnsPageResponse() throws Exception {
    // Arrange
    when(auditTrailService.getAuditTrail(
            anyString(), anyString(), anyInt(), anyInt(), any(), any(), any()))
        .thenReturn(pageResponse);

    // Act
    ResponseEntity<PageResponse<AuditTrailDto>> response =
        auditTrailController.list(CORRELATION_ID, 1, 10, null, null, null);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(pageResponse, response.getBody());

    // Verify
    verify(auditTrailService)
        .getAuditTrail(anyString(), anyString(), eq(1), eq(10), any(), any(), any());
  }

  @Test
  @DisplayName("list Service Throws Exception Wrapped into GeneralStatusCodeErrorException")
  void list_ServiceThrowsException_ReturnsGeneralStatusCodeErrorException() throws Exception {
    // Arrange
    when(auditTrailService.getAuditTrail(
            anyString(), anyString(), anyInt(), anyInt(), any(), any(), any()))
        .thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    GeneralStatusCodeErrorException exception =
        assertThrows(
            GeneralStatusCodeErrorException.class,
            () ->
                auditTrailController.list(
                    CORRELATION_ID, 1, 10, "USER", "2026-01-01", "2026-01-02"));

    assertNotNull(exception);

    // Verify
    verify(auditTrailService)
        .getAuditTrail(
            anyString(),
            anyString(),
            eq(1),
            eq(10),
            eq("USER"),
            eq("2026-01-01"),
            eq("2026-01-02"));
  }

  @Test
  @DisplayName("list With Filters Success")
  void list_WithFilters_Success() throws Exception {
    // Arrange
    when(auditTrailService.getAuditTrail(
            anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString(), anyString()))
        .thenReturn(pageResponse);

    // Act
    ResponseEntity<PageResponse<AuditTrailDto>> response =
        auditTrailController.list(CORRELATION_ID, 2, 20, "PRODUCT", "2026-01-01", "2026-01-10");

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(pageResponse, response.getBody());

    // Verify
    verify(auditTrailService)
        .getAuditTrail(
            anyString(),
            anyString(),
            eq(2),
            eq(20),
            eq("PRODUCT"),
            eq("2026-01-01"),
            eq("2026-01-10"));
  }

  @Test
  @DisplayName("list Default Params Works")
  void list_DefaultParams_Works() throws Exception {
    // Arrange
    when(auditTrailService.getAuditTrail(
            anyString(), anyString(), anyInt(), anyInt(), any(), any(), any()))
        .thenReturn(pageResponse);

    // Act
    ResponseEntity<PageResponse<AuditTrailDto>> response =
        auditTrailController.list(CORRELATION_ID, 0, 0, null, null, null);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Verify defaults passed correctly
    verify(auditTrailService)
        .getAuditTrail(anyString(), anyString(), eq(0), eq(0), any(), any(), any());
  }
}
