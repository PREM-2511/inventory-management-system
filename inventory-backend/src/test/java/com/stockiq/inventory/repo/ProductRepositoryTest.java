package com.stockiq.inventory.repo;

import com.stockiq.inventory.common.enums.product.ProductStatus;
import com.stockiq.inventory.common.enums.stock.StockLevel;
import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.entity.Product;
import com.stockiq.inventory.repository.impl.ProductRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Product Repository Test")
@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

  private final String X_CORRELATION_ID = "X-Correlation-Id";
  private final String LOG_KEY = "test-log-key";

  @Mock private NamedParameterJdbcTemplate jdbcTemplate;

  @InjectMocks private ProductRepositoryImpl productRepository;

  private Product product;

  @BeforeEach
  void setUp() {
    product = new Product();
    product.setId(1L);
    product.setName("Test Product");
    product.setCategoryId(1L);
    product.setQty(10);
    product.setReorderPoint(2);
  }

  @Test
  @DisplayName("save returns generated id when success")
  void save_ReturnsId_WhenSuccess() {
    // Arrange
    Map<String, Object> keyMap = Map.of("id", 1L);

    when(jdbcTemplate.update(
            anyString(),
            any(BeanPropertySqlParameterSource.class),
            any(KeyHolder.class),
            any(String[].class)))
        .thenAnswer(
            invocation -> {
              KeyHolder kh = invocation.getArgument(2);
              kh.getKeyList().add(keyMap);
              return 1;
            });

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = productRepository.save(X_CORRELATION_ID, LOG_KEY, product);

      // Assert
      assertEquals(1L, result);

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
    when(jdbcTemplate.update(
            anyString(),
            any(BeanPropertySqlParameterSource.class),
            any(KeyHolder.class),
            any(String[].class)))
        .thenReturn(1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = productRepository.save(X_CORRELATION_ID, LOG_KEY, product);

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
  @DisplayName("update returns affected rows")
  void update_ReturnsRows_WhenSuccess() {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(BeanPropertySqlParameterSource.class))).thenReturn(1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      int result = productRepository.update(X_CORRELATION_ID, LOG_KEY, product);

      // Assert
      assertEquals(1, result);

      // Verify
      verify(jdbcTemplate, times(1)).update(anyString(), any(BeanPropertySqlParameterSource.class));
    }
  }

  @Test
  @DisplayName("delete returns affected rows")
  void delete_ReturnsRows_WhenSuccess() {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      int result = productRepository.delete(X_CORRELATION_ID, LOG_KEY, 1L);

      // Assert
      assertEquals(1, result);

      // Verify
      verify(jdbcTemplate, times(1)).update(anyString(), any(MapSqlParameterSource.class));
    }
  }

  @Test
  @DisplayName("findById returns product dto")
  void findById_ReturnsData_WhenExists() {
    // Arrange
    ResultSet rs = mockProductResultSet(1L, "Test Product", "Electronics", 10, 2);

    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
        .thenAnswer(
            invocation -> {
              @SuppressWarnings("unchecked")
              RowMapper<ProductDto> mapper = invocation.getArgument(2);
              return List.of(mapper.mapRow(rs, 0));
            });

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      Optional<ProductDto> result = productRepository.findById(X_CORRELATION_ID, LOG_KEY, 1L);

      // Assert
      assertTrue(result.isPresent());
        assertEquals(ProductStatus.IN_STOCK, result.get().getStatus());
        assertEquals(StockLevel.NORMAL, result.get().getStockLevel());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class));
    }
  }

  @Test
  @DisplayName("findByName returns product")
  void findByName_ReturnsData_WhenExists() {
    // Arrange
    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(product));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      Optional<Product> result =
          productRepository.findByName(X_CORRELATION_ID, LOG_KEY, "Test Product");

      // Assert
      assertTrue(result.isPresent());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }
  }

  @Test
  @DisplayName("findAll with filters")
  void findAll_WithFilters() {
    // Arrange
    ResultSet rs = mockProductResultSet(1L, "Test Product", "Electronics", 10, 2);

    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
        .thenAnswer(
            invocation -> {
              @SuppressWarnings("unchecked")
              RowMapper<ProductDto> mapper = invocation.getArgument(2);
              return List.of(mapper.mapRow(rs, 0));
            });

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      List<ProductDto> result =
          productRepository.findAll(X_CORRELATION_ID, LOG_KEY, 1, 10, "test", "Electronics");

      // Assert
      assertEquals(1, result.size());
        assertEquals(ProductStatus.IN_STOCK, result.get(0).getStatus());
        assertEquals(StockLevel.NORMAL, result.get(0).getStockLevel());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class));
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
      long result = productRepository.countAll(X_CORRELATION_ID, LOG_KEY, "test", "Electronics");

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
      long result = productRepository.countAll(X_CORRELATION_ID, LOG_KEY, null, null);

      // Assert
      assertEquals(0L, result);
    }
  }

  @Test
  @DisplayName("getLowStockAlerts returns list")
  void getLowStock_ReturnsData() {
    // Arrange
    ResultSet rs = mockProductResultSet(1L, "Test Product", "Electronics", 2, 5);

    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
        .thenAnswer(
            invocation -> {
              @SuppressWarnings("unchecked")
              RowMapper<ProductDto> mapper = invocation.getArgument(2);
              return List.of(mapper.mapRow(rs, 0));
            });

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      List<ProductDto> result = productRepository.getLowStockAlerts(X_CORRELATION_ID, LOG_KEY);

      // Assert
      assertEquals(1, result.size());
        assertEquals(ProductStatus.CRITICAL, result.get(0).getStatus());
        assertEquals(StockLevel.CRITICAL, result.get(0).getStockLevel());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class));
    }
  }

  @Test
  @DisplayName("getAllLowStockProducts returns all low stock products")
  void getAllLowStockProducts_ReturnsData() {
    // Arrange
    ResultSet rs = mockProductResultSet(1L, "Test Product", "Electronics", 2, 5);

    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
        .thenAnswer(
            invocation -> {
              @SuppressWarnings("unchecked")
              RowMapper<ProductDto> mapper = invocation.getArgument(2);
              return List.of(mapper.mapRow(rs, 0));
            });

    // Act
    List<ProductDto> result = productRepository.getAllLowStockProducts(X_CORRELATION_ID, LOG_KEY);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(ProductStatus.CRITICAL, result.get(0).getStatus());
    assertEquals(StockLevel.CRITICAL, result.get(0).getStockLevel());

    // Verify
    verify(jdbcTemplate, times(1))
        .query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class));
  }

    private ResultSet mockProductResultSet(
      long id, String name, String category, int qty, int reorderPoint) {
    ResultSet rs = mock(ResultSet.class);
    Timestamp now = new Timestamp(System.currentTimeMillis());

      try {
        when(rs.getLong("id")).thenReturn(id);
        when(rs.getObject("userId")).thenReturn(1L);
        when(rs.getLong("userId")).thenReturn(1L);
        when(rs.getString("name")).thenReturn(name);
        when(rs.getString("category")).thenReturn(category);
        when(rs.getInt("qty")).thenReturn(qty);
        when(rs.getInt("reorder")).thenReturn(reorderPoint);
        when(rs.getString("createdBy")).thenReturn("admin");
        when(rs.getTimestamp("createdAt")).thenReturn(now);
        when(rs.getString("updatedBy")).thenReturn("admin");
        when(rs.getTimestamp("updatedAt")).thenReturn(now);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }

    return rs;
  }
}
