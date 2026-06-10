-- -----------------------------------------------------
-- Table `roles`
-- -----------------------------------------------------
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE
);