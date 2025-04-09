-- Миграция для заполнения базы данными

-- Вставляем данные пользователей
INSERT INTO users (name, date_of_birth, password)
VALUES ('Alice', '1990-01-15', 'user1'),
       ('Bob', '1985-05-20', 'user2'),
       ('Charlie', '1978-12-09', 'user3'),
       ('David', '1992-03-23', 'user4'),
       ('Eva', '1988-07-14', 'user5');

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
