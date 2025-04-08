CREATE TABLE IF NOT EXISTS email_data
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT              NOT NULL,
    email   VARCHAR(255) UNIQUE NOT NULL,

    CONSTRAINT fk_email_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);
