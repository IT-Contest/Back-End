-- V1__create_terms_table.sql
CREATE TABLE term (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       url VARCHAR(500) NOT NULL,
                       is_required BOOLEAN NOT NULL,
                       version VARCHAR(20),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
