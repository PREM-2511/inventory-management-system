package com.stockiq.inventory.repo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.entity.AuditTrail;
import com.stockiq.inventory.repository.impl.AuditTrailRepositoryImpl;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@DisplayName("AuditTrailRepository Test")
@ExtendWith(MockitoExtension.class)
class AuditTrailRepositoryTest {

  private static final String X_CORRELATION_ID = "X-Correlation-Id";
  private static final String LOG_KEY = "test-log-key";

  @Mock private NamedParameterJdbcTemplate jdbcTemplate;

  @InjectMocks private AuditTrailRepositoryImpl auditTrailRepository;

  private AuditTrail auditTrail;
  private Long id;

  @BeforeEach
  void setUp() {
    id = 1L;

    auditTrail =
        AuditTrail.builder()
            .id(id)
            .userId(1L)
            .userEmail("test@test.com")
            .action(1)
            .entityType(1)
            .entityId(10L)
            .entityName("CATEGORY")
            .oldValue("OLD")
            .newValue("NEW")
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .build();
  }

  @Test
  @DisplayName("save returns generated ID when insert succeeds")
  void save_ReturnsGeneratedId_WhenInsertSucceeds() throws Exception {
    // Arrange
    KeyHolder keyHolder = new GeneratedKeyHolder();
    keyHolder.getKeyList().add(Map.of("id", id));

    when(jdbcTemplate.update(
            anyString(),
            any(MapSqlParameterSource.class),
            any(KeyHolder.class),
            any(String[].class)))
        .thenAnswer(
            inv -> {
              KeyHolder kh = inv.getArgument(2);
              kh.getKeyList().add(Map.of("id", id));
              return 1;
            });

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = auditTrailRepository.save(X_CORRELATION_ID, LOG_KEY, auditTrail);

      // Assert
      assertEquals(id, result);

      // Verify
      verify(jdbcTemplate, times(1))
          .update(
              anyString(),
              any(MapSqlParameterSource.class),
              any(KeyHolder.class),
              any(String[].class));
    }
  }

  @Test
  @DisplayName("save returns 0 when key is null")
  void save_ReturnsZero_WhenKeyIsNull() throws Exception {

    // Arrange
    when(jdbcTemplate.update(
            anyString(),
            any(MapSqlParameterSource.class),
            any(KeyHolder.class),
            any(String[].class)))
        .thenReturn(1);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = auditTrailRepository.save(X_CORRELATION_ID, LOG_KEY, auditTrail);

      // Assert
      assertEquals(0L, result);
    }
  }

  @Test
  @DisplayName("findAll returns results when query succeeds")
  void findAll_ReturnsResults_WhenQuerySucceeds() throws Exception {
    // Arrange
    when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
        .thenAnswer(
            invocation -> {
              @SuppressWarnings("unchecked")
              RowMapper<AuditTrail> mapper = invocation.getArgument(2);

              // Mockito ResultSet mock (NO custom class needed)
              java.sql.ResultSet rs = mock(java.sql.ResultSet.class);

              when(rs.getLong("id")).thenReturn(1L);
              when(rs.getLong("user_id")).thenReturn(1L);
              when(rs.getString("user_email")).thenReturn("test@test.com");
              when(rs.getInt("action")).thenReturn(1);
              when(rs.getInt("entity_type")).thenReturn(1);
              when(rs.getObject("entity_id")).thenReturn(10L);
              when(rs.getString("entity_name")).thenReturn("CATEGORY");
              when(rs.getString("old_value")).thenReturn("OLD");
              when(rs.getString("new_value")).thenReturn("NEW");
              when(rs.getTimestamp("created_at"))
                  .thenReturn(new java.sql.Timestamp(System.currentTimeMillis()));

              AuditTrail result = mapper.mapRow(rs, 1);

              return List.of(result);
            });

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      List<AuditTrail> result =
          auditTrailRepository.findAll(
              X_CORRELATION_ID, LOG_KEY, 1, 10, "1", "2026-01-01", "2026-01-02");

      // Assert
      assertEquals(1, result.size());
      assertEquals(1L, result.get(0).getId());
      assertEquals("test@test.com", result.get(0).getUserEmail());

      // Verify
      verify(jdbcTemplate, times(1))
          .query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class));
    }
  }

  @Test
  @DisplayName("findAll works with no filters")
  void findAll_NoFilters() throws Exception {
    // Arrange
    when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
        .thenReturn(List.of(auditTrail));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      List<AuditTrail> result =
          auditTrailRepository.findAll(X_CORRELATION_ID, LOG_KEY, 1, 10, null, null, null);

      // Assert
      assertEquals(1, result.size());
    }
  }

  @Test
  @DisplayName("countAll returns correct count")
  void countAll_ReturnsCorrectCount() throws Exception {
    // Arrange
    when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
        .thenReturn(5L);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result =
          auditTrailRepository.countAll(X_CORRELATION_ID, LOG_KEY, "1", "2026-01-01", "2026-01-02");

      // Assert
      assertEquals(5L, result);

      // Verify
      verify(jdbcTemplate, times(1))
          .queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class));
    }
  }

  @Test
  @DisplayName("countAll returns 0 when null returned")
  void countAll_ReturnsZero_WhenNull() throws Exception {
    // Arrange
    when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
        .thenReturn(null);

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      long result = auditTrailRepository.countAll(X_CORRELATION_ID, LOG_KEY, null, null, null);

      // Assert
      assertEquals(0L, result);
    }
  }

  @Test
  @DisplayName("findAll with entityType ALL ignored filter logic still works")
  void findAll_AllEntityType() throws Exception {
    // Arrange
    when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
        .thenReturn(List.of(auditTrail));

    try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserIdAsLong).thenReturn(1L);

      // Act
      List<AuditTrail> result =
          auditTrailRepository.findAll(X_CORRELATION_ID, LOG_KEY, 1, 10, "ALL", null, null);

      // Assert
      assertEquals(1, result.size());
    }
  }
}
