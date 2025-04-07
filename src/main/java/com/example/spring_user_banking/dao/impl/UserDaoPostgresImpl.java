package com.example.spring_user_banking.dao.impl;

import com.example.spring_user_banking.dao.UserDao;
import com.example.spring_user_banking.exception.DuplicateDataException;

import com.example.spring_user_banking.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Array;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.spring_user_banking.config.database.DatabaseConstants.*;

@Repository
@RequiredArgsConstructor
public class UserDaoPostgresImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoPostgresImpl.class);

    private static final String UPDATE_USER_SQL =
            "UPDATE users SET " +
                    NAME_COLUMN + " = ?, " +
                    DATE_OF_BIRTH_COLUMN + " = ?, " +
                    PASSWORD_HASH_COLUMN + " = ? " +
                    "WHERE " + ID_COLUMN + " = ?";

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong(ID_COLUMN));
        user.setName(rs.getString(NAME_COLUMN));
        user.setDateOfBirth(rs.getObject(DATE_OF_BIRTH_COLUMN, LocalDate.class));
        user.setPasswordHash(rs.getString(PASSWORD_HASH_COLUMN)); // Прямой доступ через сеттер
        return user;
    };

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, name, date_of_birth, password FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            if (user != null) {
                loadUserDetailsWithSeparateQueries(user);
            }
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("User not found with ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Failed to find user by ID: {}", id, e);
            throw new DataAccessException("Error retrieving user with ID: " + id, e) {};
        }
    }

    @Override
    public List<User> findAll() { //todo pagination
        String sql = "SELECT id, name, date_of_birth, password FROM users";
        try {
            List<User> users = jdbcTemplate.query(sql, userRowMapper);
            users.forEach(this::loadUserDetailsWithSeparateQueries);
            return users;
        } catch (DataAccessException e) {
            logger.error("Failed to retrieve all users", e);
            throw new DataAccessException("Error retrieving all users", e) {};
        }
    }

    @Override
    @Transactional
    public boolean update(User user) {
        try {
            int updated = jdbcTemplate.update(UPDATE_USER_SQL,
                    user.getName(),
                    user.getDateOfBirth(),
                    user.getPasswordHash(),
                    user.getId());

            if (updated != AFFECTED_ROWS_ONE) {
                logger.warn("No user found to update with ID: {}", user.getId());
                return false;
            }
            return true;
        } catch (DataAccessException e) {
            logger.error("Failed to update user with ID: {}", user.getId(), e);
            throw new DataAccessException("Error updating user with ID: " + user.getId(), e) {};
        }
    }

    @Override
    public boolean addEmail(Long userId, String email) {
        String sql = "INSERT INTO email_data (user_id, email) VALUES (?, ?)";
        try {
            int inserted = jdbcTemplate.update(sql, userId, email);
            if (inserted != AFFECTED_ROWS_ONE) {
                logger.warn("Failed to insert email for user ID: {}", userId);
                return false;
            }
            return true;
        } catch (DuplicateKeyException e) {
            logger.warn("Duplicate email attempt: {} for user ID: {}", email, userId);
            throw new DuplicateDataException("Email already exists: " + email, e);
        } catch (DataAccessException e) {
            logger.error("Failed to add email: {} for user ID: {}", email, userId, e);
            throw new DataAccessException("Error adding email for user ID: " + userId, e) {};
        }
    }

    @Override
    public boolean removeEmail(Long userId, String email) {
        String sql = "DELETE FROM email_data WHERE user_id = ? AND email = ?";
        try {
            int deleted = jdbcTemplate.update(sql, userId, email);
            if (deleted != AFFECTED_ROWS_ONE) {
                logger.warn("No email found to delete: {} for user ID: {}", email, userId);
                return false;
            }
            return true;
        } catch (DataAccessException e) {
            logger.error("Failed to delete email: {} for user ID: {}", email, userId, e);
            throw new DataAccessException("Error deleting email for user ID: " + userId, e) {};
        }
    }

    @Override
    public boolean addPhone(Long userId, String phone) {
        String sql = "INSERT INTO phone_data (user_id, phone) VALUES (?, ?)";
        try {
            int inserted = jdbcTemplate.update(sql, userId, phone);
            if (inserted != AFFECTED_ROWS_ONE) {
                logger.warn("Failed to insert phone for user ID: {}", userId);
                return false;
            }
            return true;
        } catch (DuplicateKeyException e) {
            logger.warn("Duplicate phone attempt: {} for user ID: {}", phone, userId);
            throw new DuplicateDataException("Phone already exists: " + phone, e);
        } catch (DataAccessException e) {
            logger.error("Failed to add phone: {} for user ID: {}", phone, userId, e);
            throw new DataAccessException("Error adding phone for user ID: " + userId, e) {};
        }
    }

    @Override
    public boolean removePhone(Long userId, String phone) {
        String sql = "DELETE FROM phone_data WHERE user_id = ? AND phone = ?";
        try {
            int deleted = jdbcTemplate.update(sql, userId, phone);
            if (deleted != AFFECTED_ROWS_ONE) {
                logger.warn("No phone found to delete: {} for user ID: {}", phone, userId);
                return false;
            }
            return true;
        } catch (DataAccessException e) {
            logger.error("Failed to delete phone: {} for user ID: {}", phone, userId, e);
            throw new DataAccessException("Error deleting phone for user ID: " + userId, e) {};
        }
    }

    @Override
    public List<User> findByNameStartingWith(String namePrefix) { //todo pagination
        String sql = "SELECT id, name, date_of_birth, password FROM users WHERE name LIKE ?";
        String likePattern = namePrefix + "%";
        try {
            List<User> users = jdbcTemplate.query(sql, userRowMapper, likePattern);
            users.forEach(this::loadUserDetailsWithSeparateQueries);
            return users;
        } catch (DataAccessException e) {
            logger.error("Failed to find users by name prefix: {}", namePrefix, e);
            throw new DataAccessException("Error finding users by name prefix: " + namePrefix, e) {};
        }
    }

    @Override
    public List<User> findByBirthDateAfter(LocalDate date) { //todo pagination
        String sql = "SELECT id, name, date_of_birth, password FROM users WHERE date_of_birth > ?";
        try {
            List<User> users = jdbcTemplate.query(sql, userRowMapper, date);
            users.forEach(this::loadUserDetailsWithSeparateQueries);
            return users;
        } catch (DataAccessException e) {
            logger.error("Failed to find users born after: {}", date, e);
            throw new DataAccessException("Error finding users born after: " + date, e) {};
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT u.id, u.name, u.date_of_birth, u.password " +
                "FROM users u JOIN email_data e ON u.id = e.user_id " +
                "WHERE e.email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, email);
            if (user != null) {
                loadUserDetailsWithSeparateQueries(user);
            }
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("User not found with email: {}", email);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Failed to find user by email: {}", email, e);
            throw new DataAccessException("Error retrieving user by email: " + email, e) {};
        }
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        String sql = "SELECT u.id, u.name, u.date_of_birth, u.password " +
                "FROM users u JOIN phone_data p ON u.id = p.user_id " +
                "WHERE p.phone = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, phone);
            if (user != null) {
                loadUserDetailsWithSeparateQueries(user);
            }
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("User not found with phone: {}", phone);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Failed to find user by phone: {}", phone, e);
            throw new DataAccessException("Error retrieving user by phone: " + phone, e) {};
        }
    }

    private static final String FIND_BY_EMAIL_OR_PHONE_SQL =
            "SELECT u.* FROM users u " +
                    "LEFT JOIN email_data e ON u.id = e.user_id " +
                    "LEFT JOIN phone_data p ON u.id = p.user_id " +
                    "WHERE e.email = ? OR p.phone = ?";

    @Override
    public Optional<User> findByEmailOrPhone(String login) {
        try {
            User user = jdbcTemplate.queryForObject(
                    FIND_BY_EMAIL_OR_PHONE_SQL,
                    (rs, rowNum) -> {
                        User u = new User();
                        u.setId(rs.getLong("id"));
                        u.setName(rs.getString("name"));
                        u.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                        // password_hash и другие поля по необходимости
                        return u;
                    },
                    login, login // передаем login два раза для email и phone
            );
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private void loadUserDetailsWithSeparateQueries(User user) {
        Long userId = user.getId();
        try {

            String emailSql = String.format("SELECT %s FROM %s WHERE %s = ?",
                    EMAIL_COLUMN, EMAIL_DATA_TABLE, USER_ID_COLUMN);
            List<String> emails = jdbcTemplate.query(emailSql,
                    (rs, rowNum) -> rs.getString(EMAIL_COLUMN), userId);
            user.setEmails(emails);

            String phoneSql = String.format("SELECT %s FROM %s WHERE %s = ?",
                    PHONE_COLUMN, PHONE_DATA_TABLE, USER_ID_COLUMN);
            List<String> phones = jdbcTemplate.query(phoneSql,
                    (rs, rowNum) -> rs.getString(PHONE_COLUMN), userId);
            user.setPhones(phones);

            String balanceSql = String.format("SELECT %s FROM %s WHERE %s = ?",
                    BALANCE_COLUMN, ACCOUNT_TABLE, USER_ID_COLUMN);
            BigDecimal balance = jdbcTemplate.queryForObject(balanceSql,
                    (rs, rowNum) -> rs.getBigDecimal(BALANCE_COLUMN), userId);
            user.setBalance(balance);

        } catch (DataAccessException e) {
            logger.error("Failed to load details for user ID: {}", userId, e);
            throw new DataAccessException("Error loading user details for ID: " + userId, e) {};
        }
    }

    private void loadUsersDetailsWithJoin(User user) {
        String sql = String.format( //todo refactro
                "SELECT " +
                        "  (SELECT ARRAY_AGG(%s) FROM %s WHERE %s = u.%s) AS %s, " +
                        "  (SELECT ARRAY_AGG(%s) FROM %s WHERE %s = u.%s) AS %s, " +
                        "  a.%s " +
                        "FROM %s u " +
                        "LEFT JOIN %s a ON u.%s = a.%s " +
                        "WHERE u.%s = ?",
                EMAIL_COLUMN, EMAIL_DATA_TABLE, USER_ID_COLUMN, ID_COLUMN, EMAILS_ALIAS,
                PHONE_COLUMN, PHONE_DATA_TABLE, USER_ID_COLUMN, ID_COLUMN, PHONES_ALIAS,
                BALANCE_COLUMN,
                USERS_TABLE,
                ACCOUNT_TABLE, ID_COLUMN, USER_ID_COLUMN,
                ID_COLUMN);

        jdbcTemplate.query(sql, rs -> {
            Array emailArray = rs.getArray(EMAILS_ALIAS);
            user.setEmails(emailArray != null ?
                    Arrays.asList((String[])emailArray.getArray()) : Collections.emptyList());

            Array phoneArray = rs.getArray(PHONES_ALIAS);
            user.setPhones(phoneArray != null ?
                    Arrays.asList((String[])phoneArray.getArray()) : Collections.emptyList());

            user.setBalance(rs.getBigDecimal(BALANCE_COLUMN));
        }, user.getId());
    }
}