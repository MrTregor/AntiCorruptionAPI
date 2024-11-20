package com.api.AntiCorruptionAPI.Configurations;

import com.api.AntiCorruptionAPI.Components.AuthTokenFilter;
import com.api.AntiCorruptionAPI.Components.AuthEntryPointJwt;
import com.api.AntiCorruptionAPI.Services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация безопасности веб-приложения с использованием Spring Security.
 * <p>
 * Ключевые функции:
 * - Настройка аутентификации и авторизации
 * - Конфигурация JWT-аутентификации
 * - Управление правилами доступа к эндпоинтам
 * - Настройка CORS и безопасности сессий
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    /**
     * Сервис для работы с пользовательскими данными.
     */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Обработчик неавторизованных запросов.
     */
    private final AuthEntryPointJwt unauthorizedHandler;

    /**
     * Кодировщик паролей.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param userDetailsService  сервис пользовательских данных
     * @param unauthorizedHandler обработчик неавторизованных запросов
     * @param passwordEncoder     кодировщик паролей
     */
    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService,
                             AuthEntryPointJwt unauthorizedHandler,
                             PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Создает фильтр для JWT-аутентификации.
     *
     * @return AuthTokenFilter для обработки JWT-токенов
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Настраивает провайдер аутентификации.
     * <p>
     * Использует:
     * - Кастомный сервис пользовательских данных
     * - Кодировщик паролей
     *
     * @return Настроенный DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    /**
     * Создает менеджер аутентификации.
     *
     * @param authConfig конфигурация аутентификации
     * @return AuthenticationManager для управления аутентификацией
     * @throws Exception при ошибках конфигурации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Настраивает цепочку фильтров безопасности.
     * <p>
     * Конфигурация включает:
     * - Отключение CSRF
     * - Обработку неавторизованных запросов
     * - Создание stateless сессий
     * - Правила авторизации для эндпоинтов
     *
     * @param http объект HttpSecurity для настройки
     * @return сконфигурированная цепочка SecurityFilterChain
     * @throws Exception при ошибках конфигурации
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/**").authenticated()
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Настраивает CORS (Cross-Origin Resource Sharing).
     * <p>
     * Конфигурация включает:
     * - Разрешенные источники
     * - Разрешенные методы HTTP
     * - Политику credentials
     *
     * @return WebMvcConfigurer с настройками CORS
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://localhost") // Укажите ваш домен
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}