package com.api.AntiCorruptionAPI.Components;

import com.api.AntiCorruptionAPI.Services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр аутентификации JWT для обработки и проверки токенов безопасности.
 * <p>
 * Этот фильтр перехватывает каждый HTTP-запрос, извлекает и проверяет JWT-токен,
 * устанавливая контекст безопасности для аутентифицированного пользователя.
 */
public class AuthTokenFilter extends OncePerRequestFilter {

    /**
     * Утилита для работы с JWT-токенами.
     */
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Сервис для загрузки деталей пользователя.
     */
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Логгер для записи информации о процессе аутентификации.
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * Внутренняя логика фильтрации и аутентификации для каждого запроса.
     *
     * @param request     HTTP-запрос
     * @param response    HTTP-ответ
     * @param filterChain цепочка фильтров
     * @throws ServletException при ошибках сервлета
     * @throws IOException      при ошибках ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Извлечение JWT-токена из заголовка запроса
            String jwt = parseJwt(request);

            // Проверка токена и установка аутентификации
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Получение имени пользователя из токена
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Загрузка деталей пользователя
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Создание токена аутентификации
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Установка контекста безопасности
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Логирование ошибок аутентификации
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        // Продолжение цепочки фильтров
        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает JWT-токен из заголовка Authorization.
     *
     * @param request HTTP-запрос
     * @return извлеченный JWT-токен или null
     */
    private String parseJwt(HttpServletRequest request) {
        // Получение заголовка Authorization
        String headerAuth = request.getHeader("Authorization");

        // Проверка наличия и корректности Bearer-токена
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}