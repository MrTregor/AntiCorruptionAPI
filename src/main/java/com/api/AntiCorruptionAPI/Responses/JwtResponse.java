package com.api.AntiCorruptionAPI.Responses;

/**
 * Представляет ответ с JWT токеном для аутентификации пользователя.
 * <p>
 * Содержит основную информацию о токене и пользователе после успешной авторизации.
 */
public record JwtResponse(String token, Long id, String username) {

}
