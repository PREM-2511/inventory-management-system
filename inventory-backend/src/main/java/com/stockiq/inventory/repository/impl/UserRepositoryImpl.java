package com.stockiq.inventory.repository.impl;

import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.entity.User;
import com.stockiq.inventory.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private static final String REPO_IMPL = "user_repo_impl";

  private static final String INIT_LOG = "init_log";

  private static final String SELECT_ALL = "SELECT * FROM users";

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public UserRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Save User
   *
   * @param correlationId String
   * @param logKey String
   * @param user User
   * @return long
   */
  @Override
  public long save(String correlationId, String logKey, User user) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        INSERT INTO users
        (
            full_name,
            company_name,
            email,
            password,
            role_id,
            created_by,
            created_at
        )
        VALUES
        (
            :fullName,
            :companyName,
            :email,
            :password,
            :roleId,
            :createdBy,
            :createdAt
        )
        """;

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        sql, new BeanPropertySqlParameterSource(user), keyHolder, new String[] {"id"});

    return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0L;
  }

  /**
   * Find User By Email
   *
   * @param correlationId String
   * @param logKey String
   * @param email String
   * @return Optional<User>
   */
  @Override
  public Optional<User> findByEmail(String correlationId, String logKey, String email) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql = SELECT_ALL + " WHERE email = :email";

    List<User> users =
        jdbcTemplate.query(
            sql,
            new MapSqlParameterSource("email", email),
            BeanPropertyRowMapper.newInstance(User.class));

    return users.stream().findFirst();
  }

  /**
   * Update User
   *
   * @param correlationId String
   * @param logKey String
   * @param user User
   * @return int
   */
  @Override
  public int update(String correlationId, String logKey, User user) {

    ActivityLog.logInfo(correlationId, logKey, REPO_IMPL, INIT_LOG);

    String sql =
        """
        UPDATE users
        SET full_name = :fullName,
            company_name = :companyName,
            email = :email,
            password = :password,
            created_by = :createdBy,
            user_id = :userId,
            updated_by = :updatedBy,
            updated_at = :updatedAt
        WHERE id = :id
        """;

    return jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(user));
  }
}
