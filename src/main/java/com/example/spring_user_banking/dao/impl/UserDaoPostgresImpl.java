package com.example.spring_user_banking.dao.impl;

import com.example.spring_user_banking.dao.UserDao;
import com.example.spring_user_banking.exception.DuplicateDataException;
import com.example.spring_user_banking.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Array;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoPostgresImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoPostgresImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDaoPostgresImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id")); //fixme константы!
        user.setName(rs.getString("name"));
        user.setDateOfBirth(rs.getObject("date_of_birth", LocalDate.class));
        user.setPassword(rs.getString("password"));
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
    public List<User> findAll() {
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
    public boolean update(User user) {
        String sql = "UPDATE users SET name = ?, date_of_birth = ?, password = ? WHERE id = ?";
        try {
            int updated = jdbcTemplate.update(sql,
                    user.getName(),
                    user.getDateOfBirth(),
                    user.getPassword(),
                    user.getId());

            if (updated != 1) { //fixme не нравятся эти переменные в коде, позже вынести в константы
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
            if (inserted != 1) {
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
            if (deleted != 1) {
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
            if (inserted != 1) {
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
            if (deleted != 1) {
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
    public List<User> findByNameStartingWith(String namePrefix) {
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
    public List<User> findByBirthDateAfter(LocalDate date) {
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


    private void loadUserDetailsWithSeparateQueries(User user) {
        Long userId = user.getId();
        try {
            String emailSql = "SELECT email FROM email_data WHERE user_id = ?";
            List<String> emails = jdbcTemplate.query(emailSql,
                    (rs, rowNum) -> rs.getString("email"), userId);
            user.setEmails(emails);

            String phoneSql = "SELECT phone FROM phone_data WHERE user_id = ?";
            List<String> phones = jdbcTemplate.query(phoneSql,
                    (rs, rowNum) -> rs.getString("phone"), userId);
            user.setPhones(phones);

            String balanceSql = "SELECT balance FROM account WHERE user_id = ?";
            BigDecimal balance = jdbcTemplate.queryForObject(balanceSql,
                    (rs, rowNum) -> rs.getBigDecimal("balance"), userId);
            user.setBalance(balance);

        } catch (DataAccessException e) {
            logger.error("Failed to load details for user ID: {}", userId, e);
            throw new DataAccessException("Error loading user details for ID: " + userId, e) {};
        }
    }


    private void loadUsersDetailsWithJoin(User user) {
        String sql = "SELECT " +
                "  (SELECT ARRAY_AGG(email) FROM email_data WHERE user_id = u.id) AS emails, " +
                "  (SELECT ARRAY_AGG(phone) FROM phone_data WHERE user_id = u.id) AS phones, " +
                "  a.balance " +
                "FROM users u " +
                "LEFT JOIN account a ON u.id = a.user_id " +
                "WHERE u.id = ?";

        jdbcTemplate.query(sql, rs -> {
            Array emailArray = rs.getArray("emails"); //fixme константы!
            user.setEmails(emailArray != null ? Arrays.asList((String[])emailArray.getArray()) : List.of());

            Array phoneArray = rs.getArray("phones");
            user.setPhones(phoneArray != null ? Arrays.asList((String[])phoneArray.getArray()) : List.of());

            user.setBalance(rs.getBigDecimal("balance"));
        }, user.getId());
    }

}