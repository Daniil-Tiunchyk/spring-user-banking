package com.example.spring_user_banking.dao.impl;

import com.example.spring_user_banking.dao.UserDao;
import com.example.spring_user_banking.exception.DuplicateDataException;
import com.example.spring_user_banking.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.spring_user_banking.config.database.DatabaseConstants.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDaoPostgresImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) ->
            new User(
                    rs.getLong(ID_COLUMN),
                    rs.getString(NAME_COLUMN),
                    rs.getObject(DATE_OF_BIRTH_COLUMN, LocalDate.class),
                    rs.getString(PASSWORD_COLUMN),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    BigDecimal.ZERO
            );

    @Override
    public Optional<User> findById(final Long id) {
        final String sql = "SELECT id, name, date_of_birth, password FROM users WHERE id = ?";
        return querySingleUser(sql, id);
    }

    @Override
    public List<User> findAll(final int offset, final int limit) {
        final String sql = "SELECT id, name, date_of_birth, password FROM users OFFSET ? LIMIT ?";
        return queryUsers(sql, offset, limit);
    }

    @Override
    @Transactional
    public boolean update(final User user) {
        final String sql = "UPDATE users SET name = ?, date_of_birth = ?, password = ? WHERE id = ?";
        return executeUpdate(sql, user.getName(), user.getDateOfBirth(), user.getPassword(), user.getId());
    }

    @Override
    public boolean addEmail(final Long userId, final String email) {
        final String sql = "INSERT INTO email_data (user_id, email) VALUES (?, ?)";
        return executeInsert(sql, userId, email);
    }

    @Override
    public boolean removeEmail(final Long userId, final String email) {
        final String sql = "DELETE FROM email_data WHERE user_id = ? AND email = ?";
        return executeUpdate(sql, userId, email);
    }

    @Override
    public boolean addPhone(final Long userId, final String phone) {
        final String sql = "INSERT INTO phone_data (user_id, phone) VALUES (?, ?)";
        return executeInsert(sql, userId, phone);
    }

    @Override
    public boolean removePhone(final Long userId, final String phone) {
        final String sql = "DELETE FROM phone_data WHERE user_id = ? AND phone = ?";
        return executeUpdate(sql, userId, phone);
    }

    @Override
    public List<User> findByNameStartingWith(final String namePrefix, final int offset, final int limit) {
        final String sql = "SELECT id, name, date_of_birth, password FROM users WHERE name LIKE ? OFFSET ? LIMIT ?";
        return queryUsers(sql, namePrefix + "%", offset, limit);
    }

    @Override
    public List<User> findByBirthDateAfter(final LocalDate date, final int offset, final int limit) {
        final String sql = "SELECT id, name, date_of_birth, password FROM users WHERE date_of_birth > ? OFFSET ? LIMIT ?";
        return queryUsers(sql, date, offset, limit);
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        final String sql = "SELECT u.id, u.name, u.date_of_birth, u.password" +
                "FROM users u JOIN email_data e ON u.id = e.user_id " +
                "WHERE e.email = ?";
        return querySingleUser(sql, email);
    }

    @Override
    public Optional<User> findByPhone(final String phone) {
        final String sql = "SELECT u.id, u.name, u.date_of_birth, u.password " +
                "FROM users u JOIN phone_data p ON u.id = p.user_id " +
                "WHERE p.phone = ?";
        return querySingleUser(sql, phone);
    }

    @Override
    public Optional<User> findByEmailOrPhone(final String login) {
        final String sql = "SELECT u.id, u.name, u.date_of_birth, u.password FROM users u " +
                "LEFT JOIN email_data e ON u.id = e.user_id " +
                "LEFT JOIN phone_data p ON u.id = p.user_id " +
                "WHERE e.email = ? OR p.phone = ? LIMIT 1";
        return querySingleUser(sql, login, login);
    }

    private Optional<User> querySingleUser(final String sql, final Object... args) {
        try {
            User user = jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, args);
            if (user != null) {
                loadUserDetails(user);
            }
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            log.debug("User not found, args: {}", Arrays.toString(args));
            return Optional.empty();
        } catch (DataAccessException e) {
            log.error("Database error occurred while querying user, args: {}", Arrays.toString(args), e);
            throw e;
        }
    }

    private List<User> queryUsers(final String sql, final Object... args) {
        List<User> users = jdbcTemplate.query(sql, USER_ROW_MAPPER, args);
        users.forEach(this::loadUserDetails);
        return users;
    }

    private void loadUserDetails(final User user) {
        jdbcTemplate.query(
                "SELECT email FROM email_data WHERE user_id = ?",
                (RowCallbackHandler) rs -> user.getEmails().add(rs.getString(EMAIL_COLUMN)),
                user.getId()
        );

        jdbcTemplate.query(
                "SELECT phone FROM phone_data WHERE user_id = ?",
                (RowCallbackHandler) rs -> user.getPhones().add(rs.getString(PHONE_COLUMN)),
                user.getId()
        );

        BigDecimal balance = jdbcTemplate.queryForObject(
                "SELECT balance FROM account WHERE user_id = ?",
                BigDecimal.class,
                user.getId()
        );
        user.setBalance(balance);
    }

    private boolean executeUpdate(final String sql, final Object... args) {
        return jdbcTemplate.update(sql, args) == AFFECTED_ROWS_ONE;
    }

    private boolean executeInsert(final String sql, final Object... args) {
        try {
            return executeUpdate(sql, args);
        } catch (DuplicateKeyException e) {
            throw new DuplicateDataException("Duplicate entry: " + Arrays.toString(args), e);
        }
    }
}
