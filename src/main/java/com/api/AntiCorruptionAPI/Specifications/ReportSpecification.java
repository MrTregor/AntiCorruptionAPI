package com.api.AntiCorruptionAPI.Specifications;

import com.api.AntiCorruptionAPI.Models.Report;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Спецификация для фильтрации и поиска отчетов о коррупционных инцидентах.
 * <p>
 * Предоставляет гибкий механизм динамического построения запросов
 * с возможностью фильтрации по различным параметрам.
 */
public class ReportSpecification {

    /**
     * Создает спецификацию для фильтрации отчетов по заданным критериям.
     * <p>
     * Позволяет выполнять поиск отчетов с учетом множества параметров:
     * - Идентификатор автора отчета
     * - Диапазон даты инцидента
     * - Местоположение инцидента
     * - Вовлеченные лица
     * - Статус отчета
     * - Назначенный сотрудник
     *
     * @param reporterId        Идентификатор автора отчета
     * @param startIncidentDate Начальная дата периода инцидента
     * @param endIncidentDate   Конечная дата периода инцидента
     * @param incidentLocation  Местоположение инцидента
     * @param involvedPersons   Вовлеченные в инцидент лица
     * @param status            Статус отчета
     * @param assignedTo        Идентификатор назначенного сотрудника
     * @return Спецификация для выполнения динамического запроса
     */
    public static Specification<Report> filterReports(
            Long reporterId,
            LocalDate startIncidentDate,
            LocalDate endIncidentDate,
            String incidentLocation,
            String involvedPersons,
            Report.ReportStatus status,
            Long assignedTo
    ) {
        return (root, query, criteriaBuilder) -> {
            // Список предикатов для построения условий фильтрации
            List<Predicate> predicates = new ArrayList<>();

            // Фильтр по автору отчета
            if (reporterId != null) {
                predicates.add(criteriaBuilder.equal(root.get("reporterId"), reporterId));
            }

            // Фильтр по диапазону даты инцидента
            if (startIncidentDate != null && endIncidentDate != null) {
                predicates.add(criteriaBuilder.between(root.get("incidentDate"), startIncidentDate, endIncidentDate));
            }

            // Фильтр по местоположению инцидента с нечетким поиском
            if (incidentLocation != null && !incidentLocation.isEmpty()) {
                String[] locationParts = incidentLocation.toLowerCase().split("\\s+");
                Predicate[] locationPredicates = new Predicate[locationParts.length];

                for (int i = 0; i < locationParts.length; i++) {
                    locationPredicates[i] = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("incidentLocation")),
                            "%" + locationParts[i] + "%"
                    );
                }

                predicates.add(criteriaBuilder.and(locationPredicates));
            }

            // Фильтр по вовлеченным лицам с нечетким поиском
            if (involvedPersons != null && !involvedPersons.isEmpty()) {
                String[] personParts = involvedPersons.toLowerCase().split("\\s+");
                Predicate[] personPredicates = new Predicate[personParts.length];

                for (int i = 0; i < personParts.length; i++) {
                    personPredicates[i] = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("involvedPersons")),
                            "%" + personParts[i] + "%"
                    );
                }

                predicates.add(criteriaBuilder.and(personPredicates));
            }

            // Фильтр по статусу отчета
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Фильтр по назначенному сотруднику
            if (assignedTo != null) {
                predicates.add(criteriaBuilder.equal(root.get("assignedTo"), assignedTo));
            }

            // Объединение всех условий с помощью логического И
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}