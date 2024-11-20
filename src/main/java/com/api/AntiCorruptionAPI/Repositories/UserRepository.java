package com.api.AntiCorruptionAPI.Repositories;

import com.api.AntiCorruptionAPI.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления пользователями в системе.
 * <p>
 * Предоставляет расширенные методы для работы с пользователями.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по логину.
     *
     * @param username логин пользователя
     * @return Optional с пользователем
     */
    Optional<User> findByUsername(String username);

    /**
     * Поиск пользователей по наименованию группы доступа.
     *
     * @param groupName наименование группы
     * @return список пользователей
     */
    List<User> findByGroupsName(String groupName);
}

