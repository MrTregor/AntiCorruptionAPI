package com.api.AntiCorruptionAPI.Components;

import com.api.AntiCorruptionAPI.Models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса UserDetails для кастомизации аутентификации пользователей.
 * <p>
 * Этот класс адаптирует модель пользователя приложения к требованиям Spring Security,
 * предоставляя детали для процессов аутентификации и авторизации.
 */
public class UserDetailsImpl implements UserDetails {

    /**
     * Серийный идентификатор версии для сериализации.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Уникальный идентификатор пользователя.
     */
    @Getter
    private Long id;

    /**
     * Имя пользователя для входа в систему.
     */
    private final String username;

    /**
     * Пароль пользователя (игнорируется при сериализации JSON).
     */
    @JsonIgnore
    private String password;

    /**
     * Коллекция прав доступа пользователя.
     */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Конструктор для создания экземпляра UserDetails.
     *
     * @param id          уникальный идентификатор пользователя
     * @param username    имя пользователя
     * @param password    пароль
     * @param authorities права доступа
     */
    public UserDetailsImpl(Long id, String username, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Статический метод для создания UserDetails из модели пользователя.
     * <p>
     * Преобразует группы пользователя в authorities.
     *
     * @param user модель пользователя
     * @return экземпляр UserDetailsImpl
     */
    public static UserDetailsImpl build(User user) {
        // Преобразование групп пользователя в списки прав доступа
        List<GrantedAuthority> authorities = user.getGroups().stream()
                .map(group -> new SimpleGrantedAuthority(group.getName()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities);
    }

    /**
     * Возвращает права доступа пользователя.
     *
     * @return коллекция прав доступа
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return пароль
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Проверка срока действия аккаунта.
     *
     * @return всегда true в данной реализации
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Проверка блокировки аккаунта.
     *
     * @return всегда true в данной реализации
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Проверка срока действия учетных данных.
     *
     * @return всегда true в данной реализации
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Проверка активности аккаунта.
     *
     * @return всегда true в данной реализации
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Сравнение объектов по идентификатору.
     *
     * @param o объект для сравнения
     * @return true, если идентификаторы совпадают
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}