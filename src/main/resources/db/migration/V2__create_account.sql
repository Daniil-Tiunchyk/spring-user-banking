CREATE TABLE IF NOT EXISTS account
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT                   NOT NULL,
    balance NUMERIC(19, 2) DEFAULT 0 NOT NULL,

    CONSTRAINT fk_account_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);
