package com.api.AntiCorruptionAPI.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Запрос на обновление пароля пользователя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest implements Serializable {

    /**
     * Текущий пароль пользователя для подтверждения.
     */
    @NotBlank(message = "Текущий пароль не может быть пустым")
    @Size(min = 8, max = 100, message = "Длина текущего пароля должна быть от 8 до 100 символов")
    private String currentPassword;

    /**
     * Новый пароль пользователя.
     */
    @NotBlank(message = "Новый пароль не может быть пустым")
    @Size(min = 8, max = 100, message = "Длина нового пароля должна быть от 8 до 100 символов")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Новый пароль должен содержать минимум 8 символов, " +
                    "включая цифры, строчные и прописные буквы, специальный символ")
    private String newPassword;

    /**
     * Подтверждение нового пароля.
     */
    @NotBlank(message = "Подтверждение пароля не может быть пустым")
    @Size(min = 8, max = 100, message = "Длина подтверждения пароля должна быть от 8 до 100 символов")
    private String confirmPassword;

    /**
     * Проверка валидности данных для смены пароля.
     *
     * @return true, если данные корректны
     */
    public boolean isValid() {
        return newPassword != null &&
                confirmPassword != null &&
                newPassword.equals(confirmPassword) &&
                newPassword.length() >= 8;
    }

    /**
     * Очистка чувствительных данных.
     */
    public void sanitize() {
        this.currentPassword = null;
        this.newPassword = null;
        this.confirmPassword = null;
    }
}