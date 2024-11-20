package com.api.AntiCorruptionAPI.Requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Запрос на регистрацию нового пользователя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest implements Serializable {

    /**
     * Логин пользователя.
     */
    @NotBlank(message = "Логин не может быть пустым")
    @Size(min = 3, max = 50, message = "Длина логина должна быть от 3 до 50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Логин может содержать только буквы, цифры, подчеркивания и дефисы")
    private String username;

    /**
     * Пароль пользователя.
     */
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 100, message = "Длина пароля должна быть от 8 до 100 символов")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Пароль должен содержать минимум 8 символов, включая цифры, " +
                    "строчные и прописные буквы, специальный символ")
    private String password;

    /**
     * Электронная почта пользователя.
     */
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат электронной почты")
    private String email;

    /**
     * Имя пользователя.
     */
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Длина имени должна быть от 2 до 50 символов")
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @NotBlank(message = "Фамилия не может быть пустым")
    @Size(min = 2, max = 50, message = "Длина фамилии должна быть от 2 до 50 символов")
    private String lastName;

    /**
     * Проверка валидности данных регистрации.
     *
     * @return true, если данные корректны
     */
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
                password != null && password.length() >= 8 &&
                email != null && email.contains("@") &&
                firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty();
    }

    /**
     * Очистка чувствительных данных.
     */
    public void sanitize() {
        this.password = null; // Очистка пароля после использования
    }
}