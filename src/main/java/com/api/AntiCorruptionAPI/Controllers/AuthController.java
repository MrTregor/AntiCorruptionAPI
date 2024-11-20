package com.api.AntiCorruptionAPI.Controllers;

import com.api.AntiCorruptionAPI.Components.JwtUtils;
import com.api.AntiCorruptionAPI.Models.User;
import com.api.AntiCorruptionAPI.Requests.UpdatePasswordRequest;
import com.api.AntiCorruptionAPI.Responses.JwtResponse;
import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import com.api.AntiCorruptionAPI.Requests.LoginRequest;
import com.api.AntiCorruptionAPI.Requests.RegisterRequest;
import com.api.AntiCorruptionAPI.Components.UserDetailsImpl;
import com.api.AntiCorruptionAPI.Services.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Контроллер аутентификации и управления пользователями.
 * <p>
 * Основные функции:
 * - Аутентификация пользователей
 * - Регистрация новых пользователей
 * - Обновление паролей
 * <p>
 * Обеспечивает безопасность через JWT и контроль доступа
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Логгер для записи системных событий и ошибок.
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * Менеджер аутентификации для проверки учетных данных.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Утилита для работы с JWT-токенами.
     */
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Сервис для работы с пользовательскими данными.
     */
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Аутентификация пользователя.
     *
     * @param loginRequest запрос с учетными данными
     * @return JWT-токен и информация о пользователе
     */
    @PostMapping("/login")
    public ResponseEntity<ServiceResponse<JwtResponse>> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Аутентификация пользователя
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Установка контекста безопасности
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Генерация JWT-токена
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Извлечение детальной информации о пользователе
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Создание JWT-ответа
            JwtResponse jwtResponse = new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername()
            );

            return ResponseEntity.ok(
                    new ServiceResponse<>(
                            jwtResponse,
                            "Аутентификация успешна.",
                            HttpStatus.OK
                    )
            );
        } catch (UsernameNotFoundException e) {
            // Обработка ошибки неверных учетных данных
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>(
                            null,
                            "Неверное имя пользователя или пароль.",
                            HttpStatus.UNAUTHORIZED
                    ));
        } catch (Exception e) {
            // Обработка неожиданных ошибок
            logger.error("Ошибка аутентификации", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ServiceResponse<>(
                            null,
                            "Ошибка аутентификации: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    /**
     * Регистрация нового пользователя.
     * <p>
     * Доступно только с правом 'AddUsers'
     *
     * @param registerRequest данные для регистрации
     * @return результат регистрации
     */
    @PostMapping("/register")
    @PreAuthorize("hasAuthority('AddUsers')")
    public ResponseEntity<ServiceResponse<String>> registerUser(
            @Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Регистрация пользователя через сервис
            User newUser = userDetailsService.registerUser(
                    registerRequest.getUsername(),
                    registerRequest.getPassword()
            );

            return ResponseEntity.ok(
                    new ServiceResponse<>(
                            "Пользователь зарегистрирован: " + newUser.getUsername(),
                            null,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            // Обработка ошибок регистрации
            logger.error("Ошибка регистрации", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ServiceResponse<>(
                            null,
                            "Ошибка регистрации: " + e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));
        }
    }

    /**
     * Обновление пароля пользователя.
     * <p>
     * Доступно только с правом 'UpdateUsers'
     *
     * @param userId                идентификатор пользователя
     * @param updatePasswordRequest новый пароль
     * @return результат обновления пароля
     */
    @PutMapping("/update-password/{user_id}")
    @PreAuthorize("hasAuthority('UpdateUsers')")
    public ResponseEntity<ServiceResponse<String>> updateUserPassword(
            @PathVariable("user_id") Long userId,
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest
    ) {
        try {
            // Обновление пароля через сервис
            ServiceResponse<String> response = userDetailsService.updateUserPassword(
                    userId,
                    updatePasswordRequest.getNewPassword()
            );

            return new ResponseEntity<>(response, response.status());
        } catch (ResponseStatusException e) {
            // Обработка ошибок со специфическим статусом
            logger.error("Не удалось обновить пароль", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(
                            null,
                            e.getReason(),
                            (HttpStatus) e.getStatusCode()
                    ),
                    e.getStatusCode()
            );
        } catch (Exception e) {
            // Обработка неожиданных ошибок
            logger.error("Непредвиденная ошибка при обновлении пароля", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(
                            null,
                            "Произошла непредвиденная ошибка",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}