package com.api.AntiCorruptionAPI.Services;

import com.api.AntiCorruptionAPI.Models.User;
import com.api.AntiCorruptionAPI.Repositories.UserRepository;
import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import com.api.AntiCorruptionAPI.Components.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Сервис для управления пользователями и аутентификации в системе.
 * <p>
 * Реализует основные функции работы с пользователями:
 * - Регистрация новых пользователей
 * - Обновление паролей
 * - Загрузка данных пользователей для аутентификации
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор для внедрения зависимостей репозитория и кодировщика паролей.
     *
     * @param userRepository  Репозиторий для работы с пользователями
     * @param passwordEncoder Кодировщик паролей для безопасного хэширования
     */
    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Регистрация нового пользователя в системе.
     * <p>
     * Проверяет уникальность имени пользователя,
     * хэширует пароль и сохраняет нового пользователя.
     *
     * @param username Логин нового пользователя
     * @param password Пароль нового пользователя
     * @return Зарегистрированный пользователь
     * @throws RuntimeException если имя пользователя уже занято
     */
    public User registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Хэшируем пароль
        user.setIsFired(false);
        return userRepository.save(user);
    }

    /**
     * Обновление пароля для существующего пользователя.
     * <p>
     * Находит пользователя по ID, хэширует новый пароль
     * и обновляет его в базе данных.
     *
     * @param userId      Идентификатор пользователя
     * @param newPassword Новый пароль
     * @return Ответ с результатом обновления пароля
     * @throws ResponseStatusException если пользователь не найден
     */
    public ServiceResponse<String> updateUserPassword(Long userId, String newPassword) {
        // Найти пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id: " + userId
                ));

        log.info("New password: {}", newPassword);
        // Хэшировать новый пароль
        String encodedPassword = passwordEncoder.encode(newPassword);

        // Обновить пароль
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return new ServiceResponse<>(
                null,
                "Password updated successfully",
                HttpStatus.OK
        );
    }

    /**
     * Загрузка данных пользователя для аутентификации.
     * <p>
     * Используется Spring Security для проверки учетных данных
     * при входе в систему.
     *
     * @param username Имя пользователя для поиска
     * @return Детали пользователя для аутентификации
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }
}