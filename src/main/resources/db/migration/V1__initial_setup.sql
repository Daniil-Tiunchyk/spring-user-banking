-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY, -- BIGSERIAL обеспечивает автоинкремент и соответствует BIGINT
    name          VARCHAR(500) NOT NULL,
    date_of_birth DATE,                  -- Хранит дату рождения, формат ввода (например, 01.05.1993) обеспечивается на уровне приложения
    password      VARCHAR(500) NOT NULL CHECK (char_length(password) >= 8)
);

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

-- Создание таблицы email_data
CREATE TABLE IF NOT EXISTS email_data
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT       NOT NULL,
    email   VARCHAR(200) NOT NULL,
    CONSTRAINT uq_email UNIQUE (email),
    CONSTRAINT fk_email_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

-- Создание таблицы phone_data
CREATE TABLE IF NOT EXISTS phone_data
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT      NOT NULL,
    phone   VARCHAR(13) NOT NULL,
    CONSTRAINT uq_phone UNIQUE (phone),
    CONSTRAINT fk_phone_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

-- Миграция для заполнения базы данными

-- Вставляем данные пользователей (пароли обновлены согласно требованиям: min length 8)
INSERT INTO users (name, date_of_birth, password)
VALUES ('Alice', '1990-01-15', 'password1'),
       ('Bob', '1985-05-20', 'password2'),
       ('Charlie', '1978-12-09', 'password3'),
       ('David', '1992-03-23', 'password4'),
       ('Eva', '1988-07-14', 'password5');

-- Вставляем данные аккаунтов с указанием начального баланса
INSERT INTO account (user_id, balance)
VALUES (1, 1500.50),
       (2, 2350.75),
       (3, 320.00),
       (4, 800.00),
       (5, 1250.00);

-- Вставляем данные email для каждого пользователя (для некоторых более одного)
INSERT INTO email_data (user_id, email)
VALUES (1, 'alice.primary@example.com'),
       (1, 'alice.secondary@example.com'),
       (2, 'bob@example.com'),
       (3, 'charlie@example.com'),
       (3, 'charlie.alt@example.com'),
       (4, 'david@example.com'),
       (5, 'eva@example.com'),
       (5, 'eva.work@example.com');

-- Вставляем данные телефонов для каждого пользователя (для некоторых более одного)
INSERT INTO phone_data (user_id, phone)
VALUES (1, '+12345678901'),
       (1, '+12345678902'),
       (2, '+19876543210'),
       (3, '+10987654321'),
       (3, '+10987654322'),
       (4, '+11223344556'),
       (5, '+12223334455'),
       (5, '+12223334456');
