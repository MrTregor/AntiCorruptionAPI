package com.api.AntiCorruptionAPI.Repositories;

import com.api.AntiCorruptionAPI.Models.AccessGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для управления группами доступа в системе.
 * <p>
 * Предоставляет расширенные методы для работы с группами доступа.
 */
@Repository
public interface AccessGroupRepository extends JpaRepository<AccessGroup, Long> {
}