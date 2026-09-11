CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(200) NOT NULL UNIQUE ,
    account_type VARCHAR(100) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);