-- Создание таблицы аккаунтов с уникальным user_id и ограничением на неотрицательный баланс
CREATE TABLE IF NOT EXISTS account
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT         NOT NULL UNIQUE,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0 CHECK (balance >= 0),
    CONSTRAINT fk_account_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);
