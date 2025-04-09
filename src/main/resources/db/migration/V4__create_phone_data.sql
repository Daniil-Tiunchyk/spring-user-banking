-- Создание таблицы phone_data
CREATE TABLE IF NOT EXISTS phone_data
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT      NOT NULL,
    phone   VARCHAR(50) NOT NULL,
    CONSTRAINT uq_phone UNIQUE (phone),
    CONSTRAINT fk_phone_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);
