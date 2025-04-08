package com.example.spring_user_banking.config.database;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DatabaseConstants {

    // Таблицы
    public static final String USERS_TABLE = "users";
    public static final String EMAIL_DATA_TABLE = "email_data";
    public static final String PHONE_DATA_TABLE = "phone_data";
    public static final String ACCOUNT_TABLE = "account";

    // Колонки
    public static final String ID_COLUMN = "id";
    public static final String USER_ID_COLUMN = "user_id";
    public static final String NAME_COLUMN = "name";
    public static final String DATE_OF_BIRTH_COLUMN = "date_of_birth";
    public static final String PASSWORD_COLUMN = "password";
    public static final String EMAIL_COLUMN = "email";
    public static final String PHONE_COLUMN = "phone";
    public static final String BALANCE_COLUMN = "balance";

    // Алиасы
    public static final String EMAILS_ALIAS = "emails";
    public static final String PHONES_ALIAS = "phones";

    // Общие константы
    public static final int AFFECTED_ROWS_ONE = 1;
    public static final String DATE_FORMAT = "dd.MM.yyyy";
}
