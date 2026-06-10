package com.stockiq.inventory.repository;

import com.stockiq.inventory.entity.User;
import java.util.Optional;

public interface UserRepository {

  /**
   * Save User.
   *
   * @param correlationId String
   * @param logKey String
   * @param user User
   * @return long
   */
  long save(String correlationId, String logKey, User user);

  /**
   * Find User By Email.
   *
   * @param correlationId String
   * @param logKey String
   * @param email String
   * @return Optional<User>
   */
  Optional<User> findByEmail(String correlationId, String logKey, String email);
}