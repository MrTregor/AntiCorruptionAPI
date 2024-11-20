package com.api.AntiCorruptionAPI.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

/**
 * Запрос на аутентификацию пользователя.
 */
@Getter
@Setter
public class LoginRequest implements Serializable {

    /**
     * Логин пользователя.
     */
    @NotBlank(message = "Логин не может быть пустым")
    private String username;

    /**
     * Пароль пользователя.
     */
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

}