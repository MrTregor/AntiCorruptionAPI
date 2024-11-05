package com.api.AntiCorruptionAPI.Services;

import com.api.AntiCorruptionAPI.Models.Report;
import com.api.AntiCorruptionAPI.Reponses.ServiceResponse;
import com.api.AntiCorruptionAPI.Repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public ServiceResponse<Report> createReport(Report report) {
        report.setDateSubmitted(LocalDateTime.now());
        report.setStatus(Report.ReportStatus.NEW);
        report.setLastUpdated(LocalDateTime.now());
        Report savedReport = reportRepository.save(report);
        return new ServiceResponse<>(savedReport, "Report created successfully", HttpStatus.CREATED);
    }

    public ServiceResponse<Report> getReport(Long id) {
        Optional<Report> report = reportRepository.findById(id);
        return report.map(value -> new ServiceResponse<>(value, "Report found", HttpStatus.OK))
                .orElseGet(() -> new ServiceResponse<>(null, "Report not found", HttpStatus.NOT_FOUND));
    }

    public ServiceResponse<List<Report>> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        return new ServiceResponse<>(reports, "Reports retrieved successfully", HttpStatus.OK);
    }

    public ServiceResponse<Report> updateReport(Long id, Report updatedReport) {
        Optional<Report> existingReport = reportRepository.findById(id);
        if (existingReport.isPresent()) {
            Report report = existingReport.get();
            // Update fields
            report.setReporterName(updatedReport.getReporterName());
            report.setReporterContact(updatedReport.getReporterContact());
            report.setIncidentDate(updatedReport.getIncidentDate());
            report.setIncidentTime(updatedReport.getIncidentTime());
            report.setIncidentLocation(updatedReport.getIncidentLocation());
            report.setInvolvedPersons(updatedReport.getInvolvedPersons());
            report.setDescription(updatedReport.getDescription());
            report.setEvidenceDescription(updatedReport.getEvidenceDescription());
            report.setWitnesses(updatedReport.getWitnesses());
            report.setStatus(updatedReport.getStatus());
            report.setAssignedTo(updatedReport.getAssignedTo());
            report.setSolution(updatedReport.getSolution ());
            report.setLastUpdated(LocalDateTime.now());
            Report savedReport = reportRepository.save(report);
            return new ServiceResponse<>(savedReport, "Report updated successfully", HttpStatus.OK);
        } else {
            return new ServiceResponse<>(null, "Report not found", HttpStatus.NOT_FOUND);
        }
    }

    public ServiceResponse<Void> deleteReport(Long id) {
        if (reportRepository.existsById(id)) {
            reportRepository.deleteById(id);
            return new ServiceResponse<>(null, "Report deleted successfully", HttpStatus.OK);
        } else {
            return new ServiceResponse<>(null, "Report not found", HttpStatus.NOT_FOUND);
        }
    }

    public ServiceResponse<Report> assignReport(Long id, Long assignedTo) {
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
    }



    public ServiceResponse<List<Report>> getReportsByAssignedTo(Long assignedTo) {
        try {
            List<Report> reports = reportRepository.findByAssignedTo(assignedTo);
            if (reports.isEmpty()) {
                return new ServiceResponse<>(
                        null,
                        "No reports found for the assigned user.",
                        HttpStatus.NOT_FOUND
                );
            }
            return new ServiceResponse<>(
                    reports,
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
}