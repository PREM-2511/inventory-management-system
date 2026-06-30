package com.stockiq.inventory.repo;

import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.dto.StockDto;
import com.stockiq.inventory.entity.Stock;
import com.stockiq.inventory.repository.impl.StockRepositoryImpl;
import java.sql.ResultSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Timestamp;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Stock Repository Test")
@ExtendWith(MockitoExtension.class)
class StockRepositoryTest {

  private final String X_CORRELATION_ID = "X-Correlation-Id";
  private final String LOG_KEY = "test-log-key";

  @Mock private NamedParameterJdbcTemplate jdbcTemplate;

  @InjectMocks private StockRepositoryImpl stockRepository;

  private Stock stock;

  @BeforeEach
  void setUp() {
    stock = new Stock();
    stock.setId(1L);
    stock.setType(1);
    stock.setProductId(10L);
    stock.setQty(5);
    stock.setCreatedBy("admin");
    stock.setCreatedAt(new Timestamp(System.currentTimeMillis()));
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
      long result = stockRepository.save(X_CORRELATION_ID, LOG_KEY, stock);

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
      long result = stockRepository.save(X_CORRELATION_ID, LOG_KEY, stock);

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
  @DisplayName("findAll maps ResultSet correctly")
  void findAll_ReturnsMappedData() throws Exception {
    // Arrange
    ResultSet rs = mock(ResultSet.class);

    when(rs.getLong("id")).thenReturn(1L);
    when(rs.getInt("type")).thenReturn(1);
    when(rs.getString("product")).thenReturn("Laptop");
    when(rs.getInt("qty")).thenReturn(5);
    when(rs.getTimestamp("date")).thenReturn(new Timestamp(System.currentTimeMillis()));
    when(rs.getString("user")).thenReturn("admin");

    when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
        .thenAnswer(
            invocation -> {
              RowMapper<StockDto> mapper = invocation.getArgument(2);

              StockDto dto = mapper.mapRow(rs, 0);
              return List.of(dto);
            });

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      List<StockDto> result = stockRepository.findAll(X_CORRELATION_ID, LOG_KEY);

      // Assert
      assertEquals(1, result.size());
      assertEquals("Laptop", result.get(0).getProduct());
      assertEquals(5, result.get(0).getQty());

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
        .thenReturn(10L);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = stockRepository.countAll(X_CORRELATION_ID, LOG_KEY);

      // Assert
      assertEquals(10L, result);

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
      long result = stockRepository.countAll(X_CORRELATION_ID, LOG_KEY);

      // Assert
      assertEquals(0L, result);
    }
  }
}
