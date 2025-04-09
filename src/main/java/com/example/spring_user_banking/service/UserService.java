package com.example.spring_user_banking.service;

import com.example.spring_user_banking.dao.EmailDataDao;
import com.example.spring_user_banking.dao.PhoneDataDao;
import com.example.spring_user_banking.dao.UserDao;
import com.example.spring_user_banking.model.EmailData;
import com.example.spring_user_banking.model.PhoneData;
import com.example.spring_user_banking.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserDao userDao;
    private final EmailDataDao emailDataDao;
    private final PhoneDataDao phoneDataDao;

    public Optional<User> getUserById(Long userId) {
        return userDao.findById(userId);
    }

    /**
     * Реализация поиска
     */
    public List<User> searchUsers(String namePrefix,
                                  String emailExact,
                                  String phoneExact,
                                  LocalDate dateOfBirthAfter,
                                  int offset,
                                  int limit) {
        // Если нет критериев, то findAll.
        if (namePrefix == null && emailExact == null && phoneExact == null && dateOfBirthAfter == null) {
            return userDao.findAll(offset, limit);
        }

        // TODO Оптимизировать, вынеся логику в единый SQL-запрос
        Set<User> result = new HashSet<>(userDao.findAll(0, 10_000));

        if (namePrefix != null && !namePrefix.isEmpty()) {
            List<User> byName = userDao.findByNameStartingWith(namePrefix, 0, 10_000);
            result.retainAll(byName);
        }
        if (dateOfBirthAfter != null) {
            List<User> byBirth = userDao.findByBirthDateAfter(dateOfBirthAfter, 0, 10_000);
            result.retainAll(byBirth);
        }
        if (emailExact != null && !emailExact.isEmpty()) {
            var byEmail = userDao.findByEmail(emailExact);
            if (byEmail.isEmpty()) {
                return Collections.emptyList();
            } else {
                result.retainAll(Collections.singleton(byEmail.get()));
            }
        }
        if (phoneExact != null && !phoneExact.isEmpty()) {
            var byPhone = userDao.findByPhone(phoneExact);
            if (byPhone.isEmpty()) {
                return Collections.emptyList();
            } else {
                result.retainAll(Collections.singleton(byPhone.get()));
            }
        }

        List<User> filteredList = new ArrayList<>(result);
        filteredList.sort(Comparator.comparing(User::getId));

        int fromIndex = Math.min(offset, filteredList.size());
        int toIndex = Math.min(offset + limit, filteredList.size());
        return filteredList.subList(fromIndex, toIndex);
    }

    /**
     * Проверка, что текущий пользователь имеет право изменять данные указанного пользователя.
     */
    private void checkUserAuthorization(Long currentUserId, Long targetUserId) {
        if (!Objects.equals(currentUserId, targetUserId)) {
            throw new SecurityException("Нельзя изменять данные другого пользователя!");
        }
    }

    @Transactional
    public void addEmail(Long currentUserId, Long targetUserId, String email) {
        checkUserAuthorization(currentUserId, targetUserId);
        var existingUser = userDao.findByEmail(email);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(targetUserId)) {
            throw new IllegalArgumentException("Email уже занят другим пользователем!");
        }
        EmailData emailData = EmailData.builder()
                .userId(targetUserId)
                .email(email)
                .build();
        boolean saved = emailDataDao.save(emailData);
        if (!saved) {
            throw new IllegalStateException("Не удалось сохранить email (DAO вернул false).");
        }
    }

    @Transactional
    public void removeEmail(Long currentUserId, Long targetUserId, String email) {
        checkUserAuthorization(currentUserId, targetUserId);
        var allEmails = emailDataDao.findByUserId(targetUserId);
        if (allEmails.size() <= 1 && allEmails.stream().anyMatch(e -> e.getEmail().equals(email))) {
            throw new IllegalArgumentException("У пользователя должен остаться хотя бы один email!");
        }
        boolean deleted = emailDataDao.deleteByUserIdAndEmail(targetUserId, email);
        if (!deleted) {
            throw new IllegalStateException("Не удалось удалить email (DAO вернул false).");
        }
    }

    @Transactional
    public void addPhone(Long currentUserId, Long targetUserId, String phone) {
        checkUserAuthorization(currentUserId, targetUserId);
        var existingUser = userDao.findByPhone(phone);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(targetUserId)) {
            throw new IllegalArgumentException("Телефон уже занят другим пользователем!");
        }
        PhoneData phoneData = PhoneData.builder()
                .userId(targetUserId)
                .phone(phone)
                .build();
        boolean saved = phoneDataDao.save(phoneData);
        if (!saved) {
            throw new IllegalStateException("Не удалось сохранить телефон (DAO вернул false).");
        }
    }

    @Transactional
    public void removePhone(Long currentUserId, Long targetUserId, String phone) {
        checkUserAuthorization(currentUserId, targetUserId);
        var phones = phoneDataDao.findByUserId(targetUserId);
        if (phones.size() <= 1 && phones.stream().anyMatch(p -> p.getPhone().equals(phone))) {
            throw new IllegalArgumentException("У пользователя должен остаться хотя бы один телефон!");
        }
        boolean deleted = phoneDataDao.deleteByUserIdAndPhone(targetUserId, phone);
        if (!deleted) {
            throw new IllegalStateException("Не удалось удалить телефон (DAO вернул false).");
        }
    }
}
