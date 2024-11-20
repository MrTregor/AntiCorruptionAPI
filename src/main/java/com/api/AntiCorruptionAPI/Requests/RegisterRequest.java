package com.api.AntiCorruptionAPI.Requests;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

/**
 * Запрос на регистрацию нового пользователя.
 */
@Getter
@Setter
public class RegisterRequest implements Serializable {

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