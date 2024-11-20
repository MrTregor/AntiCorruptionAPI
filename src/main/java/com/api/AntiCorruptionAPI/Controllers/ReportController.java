package com.api.AntiCorruptionAPI.Controllers;

import com.api.AntiCorruptionAPI.Models.Report;
import com.api.AntiCorruptionAPI.Models.ReportDTO;
import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import com.api.AntiCorruptionAPI.Components.SecurityUtils;
import com.api.AntiCorruptionAPI.Services.ReportService;
import com.api.AntiCorruptionAPI.Services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private UserService userService;

    @PostMapping()
    @PreAuthorize("hasAuthority('CreateReport')")
    public ResponseEntity<ServiceResponse<Report>> createReport(@RequestBody Report report) {
        ServiceResponse<Report> response = reportService.createReport(report);
        return new ResponseEntity<>(response, response.status());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ViewReport')")
    public ResponseEntity<ServiceResponse<Report>> getReport(@PathVariable Long id) {
        // Получаем отчет
        ServiceResponse<Report> response = reportService.getReport(id);

        // Если отчет не найден, возвращаем текущий ответ
        if (response.data() == null || response.status() != HttpStatus.OK) {
            return new ResponseEntity<>(response, response.status());
        }

        // Проверяем права доступа
        if (securityUtils.isUserInViewAllReportsGroup()) {
            // Если пользователь может видеть все отчеты, возвращаем отчет
            return new ResponseEntity<>(response, response.status());
        } else {
            // Получаем ID текущего пользователя
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Long userId = userService.getUserIdByUsername(username);

            // Проверяем, принадлежит ли отчет текущему пользователю
            if (userId != null && userId.equals(response.data().getAssignedTo())) {
                return new ResponseEntity<>(response, response.status());
            } else {
                // Если отчет не принадлежит пользователю, возвращаем ошибку доступа
                return new ResponseEntity<>(
                        new ServiceResponse<>(null, "Access denied", HttpStatus.FORBIDDEN),
                        HttpStatus.FORBIDDEN
                );
            }
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ViewReport')")
    public ResponseEntity<ServiceResponse<List<ReportDTO>>> getAllReports() {
        if (securityUtils.isUserInViewAllReportsGroup()) {
            ServiceResponse<List<ReportDTO>> response = reportService.getAllReports();
            return new ResponseEntity<>(response, response.status());
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Long userId = userService.getUserIdByUsername(username);
            if (userId == null) {
                return new ResponseEntity<>(new ServiceResponse<>(null, "User  not found", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
            }
            ServiceResponse<List<ReportDTO>> response = reportService.getReportsByAssignedTo(userId);
            return new ResponseEntity<>(response, response.status());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UpdateReport')")
    public ResponseEntity<ServiceResponse<Report>> updateReport(@PathVariable Long id, @RequestBody Report report) {
        // Проверяем доступ к отчету
        ServiceResponse<Report> existingReport = reportService.getReport(id);
        if (existingReport.data() == null) {
            return new ResponseEntity<>(existingReport, existingReport.status());
        }

        if (checkAccessToAllReports(existingReport)) return new ResponseEntity<>(
                new ServiceResponse<>(null, "Access denied", HttpStatus.FORBIDDEN),
                HttpStatus.FORBIDDEN
        );

        // Убедимся, что assignedTo не изменяется через этот метод
        report.setAssignedTo(null);
        ServiceResponse<Report> response = reportService.updateReport(id, report);
        return new ResponseEntity<>(response, response.status());
    }

    @PatchMapping("/{id}/solution")
    @PreAuthorize("hasAuthority('SolveReport')")
    public ResponseEntity<ServiceResponse<Report>> updateSolution(
            @PathVariable Long id,
            @RequestBody String solution) {

        // Получаем существующий отчет
        ServiceResponse<Report> existingReport = reportService.getReport(id);
        if (existingReport.data() == null) {
            return new ResponseEntity<>(
                    new ServiceResponse<>(null, existingReport.message(), existingReport.status()),
                    existingReport.status()
            );
        }

        // Обновляем решение
        existingReport.data().setSolution(solution);
        ServiceResponse<Report> response = reportService.updateReport(id, existingReport.data());
        return new ResponseEntity<>(response, response.status());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SolveReport')")
    public ResponseEntity<ServiceResponse<Report>> updateStatus(
            @PathVariable Long id,
            @RequestBody Report.ReportStatus status) {

        // Получаем существующий отчет
        ServiceResponse<Report> existingReport = reportService.getReport(id);
        if (existingReport.data() == null) {
            return new ResponseEntity<>(
                    new ServiceResponse<>(null, existingReport.message(), existingReport.status()),
                    existingReport.status()
            );
        }

        // Обновляем статус
        existingReport.data().setStatus(status);
        ServiceResponse<Report> response = reportService.updateReport(id, existingReport.data());
        return new ResponseEntity<>(response, response.status());
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DeleteReport')")
    public ResponseEntity<ServiceResponse<Void>> deleteReport(@PathVariable Long id) {
        // Проверяем доступ к отчету
        ServiceResponse<Report> existingReport = reportService.getReport(id);
        if (existingReport.data() == null) {
            return new ResponseEntity<>(
                    new ServiceResponse<>(null, existingReport.message(), existingReport.status()),
                    existingReport.status()
            );
        }

        if (checkAccessToAllReports(existingReport)) return new ResponseEntity<>(
                new ServiceResponse<>(null, "Access denied", HttpStatus.FORBIDDEN),
                HttpStatus.FORBIDDEN
        );

        ServiceResponse<Void> response = reportService.deleteReport(id);
        return new ResponseEntity<>(response, response.status());
    }

    private boolean checkAccessToAllReports(ServiceResponse<Report> existingReport) {
        if (!securityUtils.isUserInViewAllReportsGroup()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Long userId = userService.getUserIdByUsername(username);

            return userId == null || !userId.equals(existingReport.data().getAssignedTo());
        }
        return false;
    }

    // Новый метод для назначения сотрудника для обработки сообщения
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('AssignProcessReport')")
    public ResponseEntity<ServiceResponse<Report>> assignReport(@PathVariable Long id, @RequestParam Long assignedTo) {
        ServiceResponse<Report> response = reportService.assignReport(id, assignedTo);
        return new ResponseEntity<>(response, response.status());
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('ViewReport')")
    public ResponseEntity<ServiceResponse<List<ReportDTO>>> filterReports(
            @RequestParam(required = false) Long reporterId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startIncidentDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endIncidentDate,
            @RequestParam(required = false) String incidentLocation,
            @RequestParam(required = false) String involvedPersons,
            @RequestParam(required = false) Report.ReportStatus status,
            @RequestParam(required = false) Long assignedTo
    ) {
        // Проверка доступа к фильтрации
        if (!securityUtils.isUserInViewAllReportsGroup()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Если пользователь не может видеть все отчеты,
            // то фильтрация только по его собственным отчетам
            reporterId = userService.getUserIdByUsername(username);
        }

        ServiceResponse<List<ReportDTO>> response = reportService.filterReports(
                reporterId,
                startIncidentDate,
                endIncidentDate,
                incidentLocation,
                involvedPersons,
                status,
                assignedTo
        );

        return new ResponseEntity<>(response, response.status());
    }
}