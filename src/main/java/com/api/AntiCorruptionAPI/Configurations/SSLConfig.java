package com.api.AntiCorruptionAPI.Configurations;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация SSL (Secure Sockets Layer) для обеспечения безопасного
 * HTTP-соединения в приложении.
 * <p>
 * Класс настраивает перенаправление с HTTP на HTTPS и применяет
 * механизмы принудительного использования защищенного соединения.
 * <p>
 * Основные функции:
 * - Принудительное использование HTTPS
 * - Перенаправление HTTP-запросов на защищенный порт
 * - Установка Constraint безопасности для всех эндпоинтов
 */
@Configuration
public class SSLConfig {

    /**
     * Настройка ServletWebServerFactory для конфигурации Tomcat с SSL.
     * <p>
     * Методы:
     * - Создание фабрики сервлетов с кастомной конфигурацией
     * - Добавление механизма перенаправления соединений
     *
     * @return Настроенная фабрика ServletWebServerFactory
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        // Создание фабрики Tomcat с кастомной постобработкой контекста
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                // СозданиеConstraint для принудительного использования защищенного соединения
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");

                // ПрименениеConstraint ко всем URL-паттернам
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);

                // Добавление constraaint в контекст сервера
                context.addConstraint(securityConstraint);
            }
        };

        // Добавление коннектора для перенаправления HTTP на HTTPS
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    /**
     * Создание коннектора для перенаправления HTTP-запросов.
     * <p>
     * Настройки коннектора:
     * - Протокол: HTTP
     * - Входящий порт: 8080
     * - Порт перенаправления: 8443 (HTTPS)
     *
     * @return Настроенный Connector для перенаправления
     */
    private Connector redirectConnector() {
        // Создание HTTP-коннектора с NIO протоколом
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");

        // Параметры коннектора для перенаправления
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);

        return connector;
    }
}