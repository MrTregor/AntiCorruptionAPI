package com.api.AntiCorruptionAPI.Specifications;

import com.api.AntiCorruptionAPI.Models.Report;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportSpecification {

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
            List<Predicate> predicates = new ArrayList<>();

            if (reporterId != null) {
                predicates.add(criteriaBuilder.equal(root.get("reporterId"), reporterId));
            }

            if (startIncidentDate != null && endIncidentDate != null) {
                predicates.add(criteriaBuilder.between(root.get("incidentDate"), startIncidentDate, endIncidentDate));
            }

            if (incidentLocation != null && !incidentLocation.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("incidentLocation")),
                        "%" + incidentLocation.toLowerCase() + "%"
                ));
            }

            if (involvedPersons != null && !involvedPersons.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("involvedPersons")),
                        "%" + involvedPersons.toLowerCase() + "%"
                ));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (assignedTo != null) {
                predicates.add(criteriaBuilder.equal(root.get("assignedTo"), assignedTo));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}