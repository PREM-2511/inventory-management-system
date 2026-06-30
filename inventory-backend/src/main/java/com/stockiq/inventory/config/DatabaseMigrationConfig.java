package com.stockiq.inventory.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ensures that the audit_trail table exists in the database on application startup. This is needed
 * because Flyway is disabled in the default profile, so schema migrations are not applied
 * automatically.
 */
@Component
public class DatabaseMigrationConfig implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationConfig.class);

  private final JdbcTemplate jdbcTemplate;

  public DatabaseMigrationConfig(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void run(ApplicationArguments args) {
    createAuditTrailTableIfNotExists();
  }

  private void createAuditTrailTableIfNotExists() {
    try {
      jdbcTemplate.execute(
          """
          CREATE TABLE IF NOT EXISTS audit_trail (
              id          BIGINT PRIMARY KEY AUTO_INCREMENT,
              user_id     BIGINT NOT NULL,
              user_email  VARCHAR(255) NOT NULL,
              action      VARCHAR(50) NOT NULL,
              entity_type VARCHAR(100) NOT NULL,
              entity_id   BIGINT,
              entity_name VARCHAR(255),
              old_value   TEXT,
              new_value   TEXT,
              created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
              CONSTRAINT fk_audit_trail_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
              INDEX idx_audit_user_id (user_id),
              INDEX idx_audit_entity (entity_type, entity_id),
              INDEX idx_audit_created (created_at)
          )
          """);
      log.info("audit_trail table initialized (created if not existing).");
    } catch (Exception e) {
      log.error("Failed to initialize audit_trail table: {}", e.getMessage());
    }
  }
}
