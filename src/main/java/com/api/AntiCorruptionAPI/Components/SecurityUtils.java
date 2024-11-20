package com.api.AntiCorruptionAPI.Components;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Утилитный класс для работы с механизмами безопасности приложения.
 * <p>
 * Предоставляет методы проверки прав доступа и привилегий пользователей
 * в контексте Spring Security.
 */
@Component
public class SecurityUtils {

    /**
     * Проверяет, имеет ли текущий аутентифицированный пользователь
     * права на просмотр всех отчетов.
     *
     * @return true, если пользователь имеет доступ к просмотру всех отчетов,
     * false в противном случае или при отсутствии аутентификации
     */
    public boolean isUserInViewAllReportsGroup() {
        // Получение текущего контекста аутентификации
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Проверка наличия и корректности аутентификации
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Проверка наличия специфической роли доступа
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority ->
                        authority.equals("AccessToAllReports")
                );
    }
}