package com.api.AntiCorruptionAPI.Responses;

import org.springframework.http.HttpStatus;

/**
 * Универсальный ответ сервиса, используемый для стандартизации
 * API-ответов с поддержкой обработки различных сценариев.
 * <p>
 * Позволяет инкапсулировать данные, сообщения и статус HTTP в едином формате.
 *
 * @param <T> Тип данных в ответе
 */
public record ServiceResponse<T>(T data, String message, HttpStatus status) {

}