package com.api.AntiCorruptionAPI.Components;

import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
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

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        logger.error("Unauthorized error: {}", authException.getMessage());

        // Создаем ServiceResponse
        ServiceResponse<Void> serviceResponse = new ServiceResponse<>(
                null,
                "Error: Unauthorized",
                HttpStatus.UNAUTHORIZED
        );

        // Устанавливаем заголовки ответа
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Преобразуем ServiceResponse в JSON и записываем в тело ответа
        objectMapper.writeValue(response.getOutputStream(), serviceResponse);
    }
}