package com.api.AntiCorruptionAPI.Models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDTO extends Report {
    private String assignedToFullName; // Для хранения ФИО сотрудника

    // Конструктор
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
