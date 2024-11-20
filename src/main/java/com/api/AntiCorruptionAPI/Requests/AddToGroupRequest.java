package com.api.AntiCorruptionAPI.Requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;

/**
 * Запрос на добавление пользователя в группу доступа.
 */
@Getter
@Setter
public class AddToGroupRequest implements Serializable {

    /**
     * Идентификатор пользователя.
     */
    @NotNull(message = "Идентификатор пользователя не может быть пустым")
    @Positive(message = "Идентификатор пользователя должен быть положительным числом")
    private Long userId;

    /**
     * Идентификатор группы доступа.
     */
    @NotNull(message = "Идентификатор группы не может быть пустым")
    @Positive(message = "Идентификатор группы должен быть положительным числом")
    private Long groupId;
}