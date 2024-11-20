package com.api.AntiCorruptionAPI.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Запрос на аутентификацию пользователя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest implements Serializable {

    /**
     * Логин пользователя.
     */
    @NotBlank(message = "Логин не может быть пустым")
    @Size(min = 3, max = 50, message = "Длина логина должна быть от 3 до 50 символов")
    private String username;

    /**
     * Пароль пользователя.
     */
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 100, message = "Длина пароля должна быть от 8 до 100 символов")
    private String password;

    /**
     * Проверка валидности учетных данных.
     *
     * @return true, если учетные данные корректны
     */
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
                password != null && password.length() >= 8;
    }

    /**
     * Очистка чувствительных данных.
     */
    public void sanitize() {
        this.password = null; // Очистка пароля после использования
    }
}