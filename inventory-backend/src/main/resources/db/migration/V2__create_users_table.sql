-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    full_name VARCHAR(100) NOT NULL,
    company_name VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NULL,
    role_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT fk_user_role
      FOREIGN KEY (role_id)
      REFERENCES roles(id)
);