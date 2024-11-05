package com.api.AntiCorruptionAPI.Controllers;

import com.api.AntiCorruptionAPI.Models.Report;
import com.api.AntiCorruptionAPI.Reponses.ServiceResponse;
import com.api.AntiCorruptionAPI.Services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping()
    @PreAuthorize("hasAuthority('CreateReport')")
    public ResponseEntity<ServiceResponse<Report>> createReport(@RequestBody Report report) {
        ServiceResponse<Report> response = reportService.createReport(report);
        return new ResponseEntity<>(response, response.status());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ViewReport')")
    public ResponseEntity<ServiceResponse<Report>> getReport(@PathVariable Long id) {
        ServiceResponse<Report> response = reportService.getReport(id);
        return new ResponseEntity<>(response, response.status());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ViewReport')")
    public ResponseEntity<ServiceResponse<List<Report>>> getAllReports() {
        ServiceResponse<List<Report>> response = reportService.getAllReports();
        return new ResponseEntity<>(response, response.status());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UpdateReport')")
    public ResponseEntity<ServiceResponse<Report>> updateReport(@PathVariable Long id, @RequestBody Report report) {
        // Убедимся, что assignedTo не изменяется через этот метод
        report.setAssignedTo(null);
        ServiceResponse<Report> response = reportService.updateReport(id, report);
        return new ResponseEntity<>(response, response.status());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DeleteReport')")
    public ResponseEntity<ServiceResponse<Void>> deleteReport(@PathVariable Long id) {
        ServiceResponse<Void> response = reportService.deleteReport(id);
        return new ResponseEntity<>(response, response.status());
    }

    // Новый метод для назначения сотрудника для обработки сообщения
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('AssignProcessReport')")
    public ResponseEntity<ServiceResponse<Report>> assignReport(@PathVariable Long id, @RequestParam Long assignedTo) {
        ServiceResponse<Report> response = reportService.assignReport(id, assignedTo);
        return new ResponseEntity<>(response, response.status());
    }
}