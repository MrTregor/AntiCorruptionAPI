package com.api.AntiCorruptionAPI.Models;

import lombok.*;

/**
 * DTO (Data Transfer Object) для передачи данных отчета с дополнительной информацией.
 * <p>
 * Используется для более гибкой передачи данных между слоями приложения,
 * включая дополнительную информацию о назначенном сотруднике.
 */
@Getter
@Setter
public class ReportDTO extends Report {
    /**
     * ФИО сотрудника, ответственного за обработку сообщения.
     */
    private String assignedToFullName;

    /**
     * Конструктор для создания DTO из сущности Report.
     *
     * @param report             исходный отчет
     * @param assignedToFullName ФИО назначенного сотрудника
     */
    public ReportDTO(Report report, String assignedToFullName) {
        // Копирование всех полей из report
        setId(report.getId());
        setDateSubmitted(report.getDateSubmitted());
        setReporterId(report.getReporterId());
        setIncidentDate(report.getIncidentDate());
        setIncidentTime(report.getIncidentTime());
        setIncidentLocation(report.getIncidentLocation());
        setInvolvedPersons(report.getInvolvedPersons());
        setDescription(report.getDescription());
        setEvidenceDescription(report.getEvidenceDescription());
        setWitnesses(report.getWitnesses());
        setStatus(report.getStatus());
        setAssignedTo(report.getAssignedTo());
        setLastUpdated(report.getLastUpdated());
        setSolution(report.getSolution());

        // Устанавливаем дополнительное поле
        this.assignedToFullName = assignedToFullName;
    }
}
