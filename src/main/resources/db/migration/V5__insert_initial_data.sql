-- Миграция для заполнения базы данными

-- Вставляем данные пользователей
INSERT INTO users (name, date_of_birth, password)
VALUES ('Alice', '1990-01-15', 'hashed_password1'),
       ('Bob', '1985-05-20', 'hashed_password2'),
       ('Charlie', '1978-12-09', 'hashed_password3');

-- Вставляем данные аккаунтов
INSERT INTO account (user_id, balance)
VALUES (1, 1500.50),
       (2, 2350.75),
       (3, 320.00);

-- Вставляем данные email
INSERT INTO email_data (user_id, email)
VALUES (1, 'alice@example.com'),
       (2, 'bob@example.com'),
       (3, 'charlie@example.com');

-- Вставляем данные телефонов
INSERT INTO phone_data (user_id, phone)
VALUES (1, '+12345678901'),
       (2, '+19876543210'),
       (3, '+10987654321');
