package com.api.AntiCorruptionAPI.Configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфигурация для настройки кодировщика паролей в приложении.
 * <p>
 * Данный класс определяет bean PasswordEncoder, который используется
 * для безопасного хеширования и проверки паролей пользователей.
 * <p>
 * BCryptPasswordEncoder предоставляет надежный механизм хеширования
 * с использованием алгоритма bcrypt, который устойчив к атакам
 * методом подбора (brute-force) и радужных таблиц.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Создает и настраивает bean PasswordEncoder.
     * <p>
     * Использует BCryptPasswordEncoder с настройками по умолчанию,
     * который генерирует надежные хеши паролей.
     *
     * @return Экземпляр PasswordEncoder для хеширования паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Создание BCryptPasswordEncoder с параметрами по умолчанию
        return new BCryptPasswordEncoder();
    }
}