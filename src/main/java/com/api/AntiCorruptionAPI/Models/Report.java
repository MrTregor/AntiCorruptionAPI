package com.api.AntiCorruptionAPI.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Модель представляет отчет о коррупционном или противоправном действии.
 *
 * Содержит полную информацию о поданном сообщении, включая детали инцидента,
 * статус обработки и результаты расследования.
 */
@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    /**
     * Уникальный идентификатор отчета.
     * Автоматически генерируется базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата и время подачи сообщения.
     * Автоматически устанавливается при создании записи.
     */
    @CreationTimestamp
    @Column(name = "date_submitted", nullable = false, updatable = false)
    private LocalDateTime dateSubmitted;

    /**
     * Идентификатор пользователя, подавшего отчет.
     */
    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    /**
     * Дата предполагаемого нарушения.
     */
    @Column(name = "incident_date")
    private LocalDate incidentDate;

    /**
     * Время предполагаемого нарушения.
     */
    @Column(name = "incident_time")
    private LocalTime incidentTime;

    /**
     * Место предполагаемого нарушения.
     */
    @Column(name = "incident_location", length = 500)
    private String incidentLocation;

    /**
     * Имена и должности вовлеченных лиц.
     */
    @Column(name = "involved_persons", length = 1000)
    private String involvedPersons;

    /**
     * Подробное описание инцидента.
     */
    @Column(name = "description", length = 2000)
    private String description;

    /**
     * Описание имеющихся доказательств.
     */
    @Column(name = "evidence_description", length = 1000)
    private String evidenceDescription;

    /**
     * Информация о свидетелях.
     */
    @Column(name = "witnesses", length = 1000)
    private String witnesses;

    /**
     * Статус обработки сообщения.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReportStatus status;

    /**
     * Идентификатор сотрудника, ответственного за обработку сообщения.
     */
    @Column(name = "assigned_to")
    private Long assignedTo;

    /**
     * Дата и время последнего обновления записи.
     * Автоматически обновляется при изменении записи.
     */
    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    /**
     * Решение по заявке.
     */
    @Column(name = "solution", length = 2000)
    private String solution;

    /**
     * Перечисление статусов обработки отчета.
     */
    public enum ReportStatus {
        /**
         * Новый, необработанный отчет.
         */
        NEW,

        /**
         * Отчет в процессе расследования.
         */
        IN_PROGRESS,

        /**
         * Расследование завершено.
         */
        CLOSED
    }

    /**
     * Переопределение метода toString для удобного вывода информации об отчете.
     *
     * @return строковое представление отчета
     */
    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", dateSubmitted=" + dateSubmitted +
                ", status=" + status +
                ", incidentLocation='" + incidentLocation + '\'' +
                '}';
    }

    /**
     * Переопределение метода equals для корректного сравнения отчетов.
     *
     * @param o объект для сравнения
     * @return true, если отчеты эквивалентны
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return id != null && id.equals(report.id);
    }

    /**
     * Переопределение метода hashCode для корректной работы в коллекциях.
     *
     * @return хеш-код отчета
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}