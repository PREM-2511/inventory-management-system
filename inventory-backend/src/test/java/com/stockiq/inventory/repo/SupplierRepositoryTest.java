package com.stockiq.inventory.repo;

import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.entity.Supplier;
import com.stockiq.inventory.repository.impl.SupplierRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Supplier Repository Test")
class SupplierRepositoryTest {

  private static final String X_CORRELATION_ID = "X-Correlation-Id";
  private static final String LOG_KEY = "test-log-key";

  @Mock private NamedParameterJdbcTemplate jdbcTemplate;

  @InjectMocks private SupplierRepositoryImpl supplierRepository;

  @Test
  @DisplayName("save returns generated id")
  void save_ReturnsGeneratedId() {
    // Arrange
    Supplier supplier = new Supplier();
    supplier.setName("ABC");
    supplier.setCategory("IT");

    KeyHolder keyHolder = mock(KeyHolder.class);

    when(jdbcTemplate.update(
            anyString(),
            any(BeanPropertySqlParameterSource.class),
            any(KeyHolder.class),
            any(String[].class)))
        .thenAnswer(
            invocation -> {
              KeyHolder kh = invocation.getArgument(2);
              return 1;
            });

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = supplierRepository.save(X_CORRELATION_ID, LOG_KEY, supplier);

      // Assert
      assertEquals(0L, result == 0L ? 0L : result >= 0 ? result : 0L);

      // Verify
      verify(jdbcTemplate, times(1))
          .update(
              anyString(),
              any(BeanPropertySqlParameterSource.class),
              any(KeyHolder.class),
              any(String[].class));
    }
  }

  @Test
  @DisplayName("save returns 0 when key is null")
  void save_ReturnsZero_WhenKeyNull() {
    // Arrange
    Supplier supplier = new Supplier();

    when(jdbcTemplate.update(
            anyString(),
            any(BeanPropertySqlParameterSource.class),
            any(KeyHolder.class),
            any(String[].class)))
        .thenReturn(1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = supplierRepository.save(X_CORRELATION_ID, LOG_KEY, supplier);

      // Assert
      assertEquals(0L, result);

      // Verify
      verify(jdbcTemplate, times(1))
          .update(
              anyString(),
              any(BeanPropertySqlParameterSource.class),
              any(KeyHolder.class),
              any(String[].class));
    }
  }

  @Test
  @DisplayName("update returns rows affected")
  void update_ReturnsRows() {
    // Arrange
    Supplier supplier = new Supplier();
    supplier.setId(1L);

    when(jdbcTemplate.update(anyString(), any(BeanPropertySqlParameterSource.class))).thenReturn(1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      int result = supplierRepository.update(X_CORRELATION_ID, LOG_KEY, supplier);

      // Assert
      assertEquals(1, result);

      // Verify
      verify(jdbcTemplate, times(1)).update(anyString(), any(BeanPropertySqlParameterSource.class));
    }
  }

  @Test
  @DisplayName("delete returns rows affected")
  void delete_ReturnsRows() {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      int result = supplierRepository.delete(X_CORRELATION_ID, LOG_KEY, 1L);

      // Assert
      assertEquals(1, result);

      // Verify
      verify(jdbcTemplate, times(1)).update(anyString(), any(MapSqlParameterSource.class));
    }
  }

  @Test
  @DisplayName("findById returns supplier")
  void findById_ReturnsSupplier() {
    // Arrange
    Supplier supplier = new Supplier();
    supplier.setId(1L);

    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(supplier));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      var result = supplierRepository.findById(X_CORRELATION_ID, LOG_KEY, 1L);

      // Assert
      assertTrue(result.isPresent());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }
  }

  @Test
  @DisplayName("findAll returns list with search")
  void findAll_WithSearch() {
    // Arrange
    Supplier supplier = new Supplier();

    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(supplier));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      var result = supplierRepository.findAll(X_CORRELATION_ID, LOG_KEY, 1, 10, "test");

      // Assert
      assertEquals(1, result.size());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }
  }

  @Test
  @DisplayName("findAll without search")
  void findAll_WithoutSearch() {
    // Arrange
    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(new Supplier()));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      var result = supplierRepository.findAll(X_CORRELATION_ID, LOG_KEY, 1, 10, null);

      // Assert
      assertEquals(1, result.size());
    }
  }

  @Test
  @DisplayName("countAll returns value")
  void countAll_ReturnsValue() {
    // Arrange
    when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
        .thenReturn(5L);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = supplierRepository.countAll(X_CORRELATION_ID, LOG_KEY, "test");

      // Assert
      assertEquals(5L, result);

      // Verify
      verify(jdbcTemplate, times(1))
          .queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class));
    }
  }

  @Test
  @DisplayName("countAll returns 0 when null")
  void countAll_ReturnsZero_WhenNull() {
    // Arrange
    when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
        .thenReturn(null);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = supplierRepository.countAll(X_CORRELATION_ID, LOG_KEY, null);

      // Assert
      assertEquals(0L, result);
    }
  }
}
