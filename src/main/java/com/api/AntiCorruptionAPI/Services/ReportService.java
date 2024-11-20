package com.api.AntiCorruptionAPI.Services;

import com.api.AntiCorruptionAPI.Components.JwtUtils;
import com.api.AntiCorruptionAPI.Models.Report;
import com.api.AntiCorruptionAPI.Models.ReportDTO;
import com.api.AntiCorruptionAPI.Models.User;
import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import com.api.AntiCorruptionAPI.Repositories.ReportRepository;
import com.api.AntiCorruptionAPI.Repositories.UserRepository;
import com.api.AntiCorruptionAPI.Specifications.ReportSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис управления отчетами о коррупционных инцидентах.
 * <p>
 * Обеспечивает полный цикл работы с отчетами:
 * - Создание новых отчетов
 * - Получение и просмотр отчетов
 * - Обновление статусов и информации
 * - Фильтрация и поиск отчетов
 */
@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository; // Добавьте это поле
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Создание нового отчета о коррупционном инциденте.
     *
     * @param report Данные для создания отчета
     * @return Ответ с созданным отчетом или ошибкой
     */
    public ServiceResponse<Report> createReport(Report report) {
        try {
            // Получаем ID пользователя из JWT токена
            Long reporterId = jwtUtils.getCurrentUserId();
            if (reporterId == null) {
                return new ServiceResponse<>(null, "Unable to identify user", HttpStatus.UNAUTHORIZED);
            }

            report.setReporterId(reporterId);
            report.setDateSubmitted(LocalDateTime.now());
            report.setStatus(Report.ReportStatus.NEW);
            report.setLastUpdated(LocalDateTime.now());
            Report savedReport = reportRepository.save(report);
            return new ServiceResponse<>(savedReport, "Report created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ServiceResponse<>(null, "Error creating report: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Получение отчета по его уникальному идентификатору.
     *
     * @param id Идентификатор отчета
     * @return Ответ с найденным отчетом или сообщением об ошибке
     */
    public ServiceResponse<Report> getReport(Long id) {
        try {
            Optional<Report> report = reportRepository.findById(id);
            return report.map(value -> new ServiceResponse<>(value, "Report found", HttpStatus.OK))
                    .orElseGet(() -> new ServiceResponse<>(null, "Report not found", HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ServiceResponse<>(null, "Error retrieving report: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Получение списка всех отчетов в системе.
     *
     * @return Ответ со списком отчетов или сообщением об ошибке
     */
    public ServiceResponse<List<ReportDTO>> getAllReports() {
        try {
            List<Report> reports = reportRepository.findAll();
            return new ServiceResponse<>(getReportsDTO(reports), "Reports retrieved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ServiceResponse<>(null, "Error retrieving reports: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Обновление существующего отчета о коррупционном инциденте.
     *
     * @param id            Идентификатор отчета для обновления
     * @param updatedReport Данные для обновления отчета
     * @return Ответ с обновленным отчетом или сообщением об ошибке
     */
    public ServiceResponse<Report> updateReport(Long id, Report updatedReport) {
        try {
            Optional<Report> existingReportOptional = reportRepository.findById(id);
            if (existingReportOptional.isPresent()) {
                Report existingReport = existingReportOptional.get();

                // Обновляем только те поля, которые не null в updatedReport
                if (updatedReport.getIncidentDate() != null) {
                    existingReport.setIncidentDate(updatedReport.getIncidentDate());
                }

                if (updatedReport.getIncidentTime() != null) {
                    existingReport.setIncidentTime(updatedReport.getIncidentTime());
                }

                if (updatedReport.getIncidentLocation() != null) {
                    existingReport.setIncidentLocation(updatedReport.getIncidentLocation());
                }

                if (updatedReport.getInvolvedPersons() != null) {
                    existingReport.setInvolvedPersons(updatedReport.getInvolvedPersons());
                }

                if (updatedReport.getDescription() != null) {
                    existingReport.setDescription(updatedReport.getDescription());
                }

                if (updatedReport.getEvidenceDescription() != null) {
                    existingReport.setEvidenceDescription(updatedReport.getEvidenceDescription());
                }

                if (updatedReport.getWitnesses() != null) {
                    existingReport.setWitnesses(updatedReport.getWitnesses());
                }

                if (updatedReport.getStatus() != null) {
                    existingReport.setStatus(updatedReport.getStatus());
                }

                // Проверка assignedTo происходит в контроллере
                if (updatedReport.getSolution() != null) {
                    existingReport.setSolution(updatedReport.getSolution());
                }

                existingReport.setLastUpdated(LocalDateTime.now());

                Report savedReport = reportRepository.save(existingReport);
                return new ServiceResponse<>(savedReport, "Report updated successfully", HttpStatus.OK);
            } else {
                return new ServiceResponse<>(null, "Report not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ServiceResponse<>(null, "Error updating report: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Удаление отчета по его идентификатору.
     *
     * @param id Идентификатор отчета для удаления
     * @return Ответ об успешности удаления или сообщение об ошибке
     */
    public ServiceResponse<Void> deleteReport(Long id) {
        try {
            if (reportRepository.existsById(id)) {
                reportRepository.deleteById(id);
                return new ServiceResponse<>(null, "Report deleted successfully", HttpStatus.OK);
            } else {
                return new ServiceResponse<>(null, "Report not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ServiceResponse<>(null, "Error deleting report: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Назначение ответственного сотрудника для отчета.
     *
     * @param id         Идентификатор отчета
     * @param assignedTo Идентификатор сотрудника, которому назначается отчет
     * @return Ответ с обновленным отчетом или сообщением об ошибке
     */
    public ServiceResponse<Report> assignReport(Long id, Long assignedTo) {
        try {
            Optional<Report> existingReport = reportRepository.findById(id);
            if (existingReport.isPresent()) {
                Report report = existingReport.get();
                report.setAssignedTo(assignedTo);
                report.setLastUpdated(LocalDateTime.now());
                Report savedReport = reportRepository.save(report);
                return new ServiceResponse<>(savedReport, "Report assigned successfully", HttpStatus.OK);
            } else {
                return new ServiceResponse<>(null, "Report not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ServiceResponse<>(null, "Error assigning report: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Получение списка отчетов, назначенных конкретному сотруднику.
     *
     * @param assignedTo Идентификатор сотрудника
     * @return Список отчетов, назначенных сотруднику
     */
    public ServiceResponse<List<ReportDTO>> getReportsByAssignedTo(Long assignedTo) {
        try {
            List<ReportDTO> reportDTOs = reportRepository.findByAssignedTo(assignedTo).stream()
                    .filter(report -> report.getAssignedTo() != null && report.getStatus() != Report.ReportStatus.CLOSED)
                    .map(report -> {
                        User assignedUser = userRepository.findById(report.getAssignedTo()).orElse(null);
                        String assignedToFullName = null;
                        if (assignedUser != null) {
                            assignedToFullName = assignedUser.getLastName() + " " +
                                    assignedUser.getFirstName() + " " +
                                    (assignedUser.getMiddleName() != null ? assignedUser.getMiddleName() : "");
                        }
                        return new ReportDTO(report, assignedToFullName);
                    })
                    .toList();
            if (reportDTOs.isEmpty()) {
                return new ServiceResponse<>(
                        null,
                        "No reports found for the assigned user.",
                        HttpStatus.NOT_FOUND
                );
            }
            return new ServiceResponse<>(
                    reportDTOs,
                    "Reports retrieved successfully.",
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ServiceResponse<>(
                    null,
                    "Error retrieving reports: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ServiceResponse<List<ReportDTO>> filterReports(
            Long reporterId,
            LocalDate startIncidentDate,
            LocalDate endIncidentDate,
            String incidentLocation,
            String involvedPersons,
            Report.ReportStatus status,
            Long assignedTo
    ) {
        try {
            Specification<Report> spec = ReportSpecification.filterReports(
                    reporterId,
                    startIncidentDate,
                    endIncidentDate,
                    incidentLocation,
                    involvedPersons,
                    status,
                    assignedTo
            );

            List<Report> reports = reportRepository.findAll(spec);

            List<ReportDTO> reportDTOs = getReportsDTO(reports);

            return new ServiceResponse<>(
                    reportDTOs,
                    "Reports filtered successfully",
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ServiceResponse<>(
                    null,
                    "Error filtering reports: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private List<ReportDTO> getReportsDTO(List<Report> reports) {
        List<ReportDTO> reportDTOs = new ArrayList<>();

        for (Report report : reports) {
            String assignedToFullName = null;
            if (report.getAssignedTo() != null) {
                User assignedUser = userRepository.findById(report.getAssignedTo()).orElse(null);
                if (assignedUser != null) {
                    assignedToFullName = assignedUser.getLastName() + " " +
                            assignedUser.getFirstName() + " " +
                            assignedUser.getMiddleName();
                }
            }
            ReportDTO reportDTO = new ReportDTO(report, assignedToFullName);
            reportDTOs.add(reportDTO);
        }
        return reportDTOs;
    }
}