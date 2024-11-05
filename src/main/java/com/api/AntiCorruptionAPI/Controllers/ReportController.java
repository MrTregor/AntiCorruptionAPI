package com.api.AntiCorruptionAPI.Controllers;

import com.api.AntiCorruptionAPI.Models.Report;
import com.api.AntiCorruptionAPI.Reponses.ServiceResponse;
import com.api.AntiCorruptionAPI.SecurityUtils;
import com.api.AntiCorruptionAPI.Services.ReportService;
import com.api.AntiCorruptionAPI.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ServiceResponse<List<Report>>> getAllReports() {
        if (securityUtils.isUserInViewAllReportsGroup()) {
            ServiceResponse<List<Report>> response = reportService.getAllReports();
            return new ResponseEntity<>(response, response.status());
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Long userId = userService.getUserIdByUsername(username);
            if (userId == null) {
                return new ResponseEntity<>(new ServiceResponse<>(null, "User not found", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
            }
            ServiceResponse<List<Report>> response = reportService.getReportsByAssignedTo(userId);
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
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('AssignProcessReport')")
    public ResponseEntity<ServiceResponse<Report>> assignReport(@PathVariable Long id, @RequestParam Long assignedTo) {
        ServiceResponse<Report> response = reportService.assignReport(id, assignedTo);
        return new ResponseEntity<>(response, response.status());
    }
}