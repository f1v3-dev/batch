CREATE TABLE point_history
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id        BIGINT                 NOT NULL,
    transaction_type ENUM ('EARN', 'SPEND') NOT NULL,
    earn_reason      ENUM ('DAILY_LOGIN', 'MISSION', 'BOOK_REGISTRATION', 'BOOK_REVIEW', 'ADMIN_ADJUSTMENT'),
    spend_reason     ENUM ('GACHA', 'ADMIN_ADJUSTMENT'),
    points           INT                    NOT NULL,
    balance_after    INT                    NOT NULL,
    description      VARCHAR(255),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    batch_processed  BOOLEAN   DEFAULT FALSE,
    INDEX idx_earn_type_reason (transaction_type, earn_reason),
    INDEX idx_spend_type_reason (transaction_type, spend_reason),
    INDEX idx_member_created (member_id, created_at),
    INDEX idx_batch_processed (batch_processed),
    CONSTRAINT check_earn_reason CHECK (
        (transaction_type = 'EARN' AND earn_reason IS NOT NULL AND spend_reason IS NULL) OR
        (transaction_type = 'SPEND' AND spend_reason IS NOT NULL AND earn_reason IS NULL)
        )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;