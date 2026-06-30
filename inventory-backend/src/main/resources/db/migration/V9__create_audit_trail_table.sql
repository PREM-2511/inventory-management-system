-- -----------------------------------------------------
-- Table `audit_trail`
-- -----------------------------------------------------
CREATE TABLE audit_trail (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    user_email  VARCHAR(255) NOT NULL,
    action INT NOT NULL,
    entity_type INT NOT NULL,
    entity_id   BIGINT,
    entity_name VARCHAR(255),
    old_value   TEXT,
    new_value   TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_trail_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_audit_user_id (user_id),
    INDEX idx_audit_entity (entity_type, entity_id),
    INDEX idx_audit_created (created_at)
);
