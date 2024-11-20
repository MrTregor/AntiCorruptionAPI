package com.api.AntiCorruptionAPI.Components;

import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Точка входа для обработки неавторизованных запросов в JWT-аутентификации.
 *
 * Реализует механизм обработки ошибок аутентификации, возвращая стандартизированный
 * JSON-ответ с информацией о неудачной попытке доступа.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    /**
     * Логгер для записи информации об ошибках аутентификации.
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * ObjectMapper для сериализации объектов в JSON.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Обрабатывает неавторизованные запросы, генерируя стандартный ответ об ошибке.
     *
     * @param request HttpServlet-запрос
     * @param response HttpServlet-ответ
     * @param authException Исключение аутентификации, вызвавшее отказ в доступе
     * @throws IOException При ошибках ввода-вывода при записи ответа
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Логирование сообщения об ошибке
        logger.error("Unauthorized error: {}", authException.getMessage());

        // Создание стандартизированного ответа с информацией об ошибке
        ServiceResponse<Void> serviceResponse = new ServiceResponse<>(
                null,
                "Error: Unauthorized",
                HttpStatus.UNAUTHORIZED
        );

        // Установка заголовков ответа для JSON-формата
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Преобразование ответа в JSON и запись в выходной поток
        objectMapper.writeValue(response.getOutputStream(), serviceResponse);
    }
}