package com.stockiq.inventory.repository;

import com.stockiq.inventory.entity.User;
import java.util.Optional;

public interface UserRepository {

  /**
   * Save User
   *
   * @param correlationId String
   * @param logKey String
   * @param user User
   * @return long
   * @throws Exception if an error occurs during the operation
   */
  long save(String correlationId, String logKey, User user) throws Exception;

  /**
   * Find User By Email
   *
   * @param correlationId String
   * @param logKey String
   * @param email String
   * @return Optional<User>
   * @throws Exception if an error occurs during the operation
   */
  Optional<User> findByEmail(String correlationId, String logKey, String email) throws Exception;

  /**
   * Update User
   *
   * @param correlationId String
   * @param logKey String
   * @param user User
   * @return int
   * @throws Exception if an error occurs during the operation
   */
  int update(String correlationId, String logKey, User user) throws Exception;
}
