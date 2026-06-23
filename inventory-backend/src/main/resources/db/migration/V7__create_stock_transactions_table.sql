-- -----------------------------------------------------
-- Table `stock_transactions`
-- -----------------------------------------------------
CREATE TABLE stock_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(10) NOT NULL, -- 'IN' or 'OUT'
    product_id BIGINT NOT NULL,
    qty INT NOT NULL,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
