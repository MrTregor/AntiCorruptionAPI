package com.api.AntiCorruptionAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Anti-Corruption API.
 * <p>
 * Служит точкой входа для Spring Boot приложения,
 * отвечающего за мониторинг и предотвращение коррупционных действий.
 * <p>
 * Основные возможности:
 * - Инициализация Spring Boot контекста
 * - Запуск всех компонентов приложения
 * - Конфигурация Spring Bean's
 */
@SpringBootApplication
public class AntiCorruptionApiApplication {

    /**
     * Точка входа в приложение.
     * <p>
     * Запускает Spring Boot приложение с переданными аргументами.
     *
     * @param args Аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(AntiCorruptionApiApplication.class, args);
    }
}