package com.api.AntiCorruptionAPI.Components;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;
import java.util.Date;

/**
 * Утилитный класс для работы с JWT (JSON Web Token).
 *
 * Предоставляет функциональность генерации, проверки и извлечения информации из JWT-токенов.
 * Поддерживает операции аутентификации и авторизации в приложении.
 */
@Component
public class JwtUtils {
    /**
     * Логгер для записи информации о JWT-операциях.
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * Секретный ключ для подписи JWT-токенов.
     */
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    /**
     * Время жизни JWT-токена в миллисекундах.
     */
    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Создает криптографический ключ для подписи токенов.
     *
     * @return Ключ для подписи JWT
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Генерирует JWT-токен на основе данных аутентификации.
     *
     * @param authentication объект аутентификации
     * @return сгенерированный JWT-токен
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("user_id", userPrincipal.getId()) // Добавляем ID пользователя
                .claim("groups", userPrincipal.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Извлекает имя пользователя из JWT-токена.
     *
     * @param token JWT-токен
     * @return имя пользователя
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Извлекает ID пользователя из JWT-токена.
     *
     * @param token JWT-токен
     * @return ID пользователя или null при ошибке
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.parseLong(claims.get("user_id").toString());
        } catch (Exception e) {
            logger.error("Error getting user ID from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Извлекает JWT-токен из текущего контекста запроса.
     *
     * @return JWT-токен или null
     */
    public String getTokenFromContext() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String bearerToken = request.getHeader("Authorization");
                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    return bearerToken.substring(7);
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("Error getting token from context: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Получает ID текущего пользователя из JWT-токена.
     *
     * @return ID пользователя или null
     */
    public Long getCurrentUserId() {
        String token = getTokenFromContext();
        if (token != null) {
            return getUserIdFromToken(token);
        }
        return null;
    }

    /**
     * Проверяет валидность JWT-токена.
     *
     * @param authToken JWT-токен для проверки
     * @return true, если токен валиден, иначе false
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}