package com.api.AntiCorruptionAPI.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class Report {

    // Уникальный идентификатор записи
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Дата и время подачи сообщения
    @Column(name = "date_submitted", nullable = false)
    private LocalDateTime dateSubmitted;

    // Имя заявителя (может быть анонимным)
    @Column(name = "reporter_name")
    private String reporterName;

    // Контактные данные заявителя (например, телефон или email)
    @Column(name = "reporter_contact")
    private String reporterContact;

    // Дата предполагаемого нарушения
    @Column(name = "incident_date")
    private LocalDate incidentDate;

    // Время предполагаемого нарушения
    @Column(name = "incident_time")
    private LocalTime incidentTime;

    // Место предполагаемого нарушения (адрес или описание локации)
    @Column(name = "incident_location")
    private String incidentLocation;

    // Имена и должности вовлеченных лиц
    @Column(name = "involved_persons")
    private String involvedPersons;

    // Подробное описание произошедшего инцидента
    @Column(name = "description")
    private String description;

    // Описание имеющихся доказательств (например, фото, видео, документы)
    @Column(name = "evidence_description")
    private String evidenceDescription;

    // Информация о свидетелях (имена и контактные данные)
    @Column(name = "witnesses")
    private String witnesses;

    // Статус обработки сообщения (например: 'new', 'in_progress', 'closed')
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReportStatus status;

    // ID сотрудника, ответственного за обработку сообщения
    @Column(name = "assigned_to")
    private Long assignedTo;

    // Дата и время последнего обновления записи
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // Решение по заявке (например, какие меры были предприняты)
    @Column(name = "solution")
    private String solution;

    // Перечисление для статуса отчета
    public enum ReportStatus {
        NEW, IN_PROGRESS, CLOSED
    }
}