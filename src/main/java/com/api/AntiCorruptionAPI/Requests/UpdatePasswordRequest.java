package com.api.AntiCorruptionAPI.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;

/**
 * Запрос на обновление пароля пользователя.
 */
@Getter
@Setter
public class UpdatePasswordRequest {
    /**
     * Новый пароль пользователя.
     */
    @NotBlank(message = "Новый пароль не может быть пустым")
    private String newPassword;

}
