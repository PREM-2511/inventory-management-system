package com.stockiq.inventory.repo;

import com.stockiq.inventory.common.enums.category.Status;
import com.stockiq.inventory.dto.CategoryDto;
import com.stockiq.inventory.entity.Category;
import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.repository.impl.CategoryRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("CategoryRepository Test")
@ExtendWith(MockitoExtension.class)
class CategoryRepositoryTest {

  private final String X_CORRELATION_ID = "X-Correlation-Id";
  private final String LOG_KEY = "test-log-key";

  @Mock private NamedParameterJdbcTemplate jdbcTemplate;

  @InjectMocks private CategoryRepositoryImpl categoryRepository;

  private Category category;
  private Long id;

  @BeforeEach
  void setUp() {
    id = 1L;

    category = new Category();
    category.setId(id);
    category.setName("Test Category");
    category.setIcon("icon.png");
    category.setStatus(1);
  }

  @Test
  @DisplayName("save returns generated ID when insert succeeds")
  void save_ReturnsGeneratedId_WhenInsertSucceeds() {
    // Arrange
    Map<String, Object> map = Map.of("id", id);

    when(jdbcTemplate.update(
            anyString(),
            any(BeanPropertySqlParameterSource.class),
            any(KeyHolder.class),
            any(String[].class)))
        .thenAnswer(
            invocation -> {
              KeyHolder kh = invocation.getArgument(2);
              kh.getKeyList().add(map);
              return 1;
            });

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = categoryRepository.save(X_CORRELATION_ID, LOG_KEY, category);

      // Assert
      assertEquals(id, result);

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
  @DisplayName("save returns 0 when generated key is null")
  void save_ReturnsZero_WhenGeneratedKeyIsNull() {
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
      long result = categoryRepository.save(X_CORRELATION_ID, LOG_KEY, category);

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
  @DisplayName("update returns rows affected when update succeeds")
  void update_ReturnsRowsAffected_WhenUpdateSucceeds() {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      int result = categoryRepository.update(X_CORRELATION_ID, LOG_KEY, category);

      // Assert
      assertEquals(1, result);

      // Verify
      verify(jdbcTemplate, times(1)).update(anyString(), any(MapSqlParameterSource.class));
    }
  }

  @Test
  @DisplayName("delete returns rows affected when delete succeeds")
  void delete_ReturnsRowsAffected_WhenDeleteSucceeds() {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      int result = categoryRepository.delete(X_CORRELATION_ID, LOG_KEY, id);

      // Assert
      assertEquals(1, result);

      // Verify
      verify(jdbcTemplate, times(1)).update(anyString(), any(MapSqlParameterSource.class));
    }
  }

  @Test
  @DisplayName("findById returns category when found")
  void findById_ReturnsCategory_WhenFound() {
    // Arrange
    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(category));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      Optional<CategoryDto> result = categoryRepository.findById(X_CORRELATION_ID, LOG_KEY, id);

      // Assert
      assertTrue(result.isPresent());
      assertEquals(id, result.get().getId());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }
  }

  @Test
  @DisplayName("findAll returns results when filters provided")
  void findAll_ReturnsResults_WhenFiltersProvided() {
    // Arrange
    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(category));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      List<Category> result =
          categoryRepository.findAll(X_CORRELATION_ID, LOG_KEY, 1, 10, "test", Status.ACTIVE);

      // Assert
      assertEquals(1, result.size());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }
  }

  @Test
  @DisplayName("findAll skips filters when search is empty")
  void findAll_SkipsFilters_WhenSearchEmpty() {
    // Arrange
    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(category));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      List<Category> result =
          categoryRepository.findAll(X_CORRELATION_ID, LOG_KEY, 1, 10, "   ", null);

      // Assert
      assertEquals(1, result.size());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }
  }

  @Test
  @DisplayName("findAll with status only")
  void findAll_StatusOnly() {
    // Arrange
    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(category));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      List<Category> result =
          categoryRepository.findAll(X_CORRELATION_ID, LOG_KEY, 1, 10, null, Status.ACTIVE);

      // Assert
      assertEquals(1, result.size());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }
  }

  @Test
  @DisplayName("findAll with no filters")
  void findAll_NoFilters() {
    // Arrange
    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(category));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      List<Category> result =
          categoryRepository.findAll(X_CORRELATION_ID, LOG_KEY, 1, 10, null, null);

      // Assert
      assertEquals(1, result.size());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }
  }

  @Test
  @DisplayName("countAll returns correct count when query succeeds")
  void countAll_ReturnsCorrectCount_WhenQuerySucceeds() {
    // Arrange
    when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
        .thenReturn(5L);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = categoryRepository.countAll(X_CORRELATION_ID, LOG_KEY, "test", Status.ACTIVE);

      // Assert
      assertEquals(5L, result);

      // Verify
      verify(jdbcTemplate, times(1))
          .queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class));
    }
  }

  @Test
  @DisplayName("countAll returns 0 when query returns null")
  void countAll_ReturnsZero_WhenQueryReturnsNull() {
    // Arrange
    when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
        .thenReturn(null);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = categoryRepository.countAll(X_CORRELATION_ID, LOG_KEY, null, null);

      // Assert
      assertEquals(0L, result);

      // Verify
      verify(jdbcTemplate, times(1))
          .queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class));
    }
  }

  @Test
  @DisplayName("findByName returns category when found")
  void findByName_ReturnsCategory_WhenFound() {
    // Arrange
    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(category));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      Optional<Category> result =
          categoryRepository.findByName(X_CORRELATION_ID, LOG_KEY, "Test Category");

      // Assert
      assertTrue(result.isPresent());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }
  }
}
