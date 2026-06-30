package com.stockiq.inventory.repo;

import com.stockiq.inventory.entity.User;
import com.stockiq.inventory.repository.impl.UserRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
@DisplayName("User Repository Test")
class UserRepositoryTest {

  private static final String X_CORRELATION_ID = "X-Correlation-Id";
  private static final String LOG_KEY = "test-log-key";

  @Mock private NamedParameterJdbcTemplate jdbcTemplate;

  @InjectMocks private UserRepositoryImpl userRepository;

  @Test
  @DisplayName("save returns generated id")
  void save_ReturnsGeneratedId() {

    //  Arrange
    User user = new User();
    user.setFullName("John Doe");
    user.setEmail("john@test.com");

    KeyHolder keyHolder = mock(KeyHolder.class);

    when(jdbcTemplate.update(
            anyString(),
            any(BeanPropertySqlParameterSource.class),
            any(KeyHolder.class),
            any(String[].class)))
        .thenAnswer(invocation -> 1);

    //  Act
    long result = userRepository.save(X_CORRELATION_ID, LOG_KEY, user);

    // Assert
    assertTrue(result >= 0);

    //  Verify -
    verify(jdbcTemplate, times(1))
        .update(
            anyString(),
            any(BeanPropertySqlParameterSource.class),
            any(KeyHolder.class),
            any(String[].class));
  }

  @Test
  @DisplayName("save returns 0 when key is null")
  void save_ReturnsZero_WhenKeyNull() {

    //  Arrange
    User user = new User();

    when(jdbcTemplate.update(
            anyString(),
            any(BeanPropertySqlParameterSource.class),
            any(KeyHolder.class),
            any(String[].class)))
        .thenReturn(1);

    //  Act
    long result = userRepository.save(X_CORRELATION_ID, LOG_KEY, user);

    // Assert
    assertEquals(0L, result);

    //  Verify
    verify(jdbcTemplate, times(1))
        .update(
            anyString(),
            any(BeanPropertySqlParameterSource.class),
            any(KeyHolder.class),
            any(String[].class));
  }

  @Test
  @DisplayName("findByEmail returns user when found")
  void findByEmail_ReturnsUser() {

    //  Arrange
    User user = new User();
    user.setEmail("test@test.com");

    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of(user));

    //  Act
    var result = userRepository.findByEmail(X_CORRELATION_ID, LOG_KEY, "test@test.com");

    //  Assert
    assertTrue(result.isPresent());
    assertEquals("test@test.com", result.get().getEmail());

    // Verify
    verify(jdbcTemplate, times(1))
        .query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
  }

  @Test
  @DisplayName("findByEmail returns empty when not found")
  void findByEmail_ReturnsEmpty() {

    //  Arrange
    when(jdbcTemplate.query(
            anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
        .thenReturn(List.of());

    //  Act
    var result = userRepository.findByEmail(X_CORRELATION_ID, LOG_KEY, "none@test.com");

    //  Assert
    assertTrue(result.isEmpty());

    // Verify
    verify(jdbcTemplate, times(1))
        .query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
  }

  @Test
  @DisplayName("update returns rows affected")
  void update_ReturnsRowsAffected() {
    //  Arrange
    User user = new User();
    user.setId(1L);

    when(jdbcTemplate.update(anyString(), any(BeanPropertySqlParameterSource.class))).thenReturn(1);

    // Act
    int result = userRepository.update(X_CORRELATION_ID, LOG_KEY, user);

    // Assert
    assertEquals(1, result);

    //  Verify
    verify(jdbcTemplate, times(1)).update(anyString(), any(BeanPropertySqlParameterSource.class));
  }
}
