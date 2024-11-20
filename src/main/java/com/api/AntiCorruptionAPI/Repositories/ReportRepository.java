package com.api.AntiCorruptionAPI.Repositories;

import com.api.AntiCorruptionAPI.Models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для управления отчетами в системе.
 * <p>
 * Предоставляет расширенные методы для работы с отчетами.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    /**
     * Поиск отчетов, назначенных конкретному сотруднику.
     *
     * @param assignedTo идентификатор сотрудника
     * @return список отчетов
     */
    List<Report> findByAssignedTo(Long assignedTo);
}