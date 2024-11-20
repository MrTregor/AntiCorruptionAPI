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

/**
 * Контроллер для управления отчетами в системе противодействия коррупции.
 *
 * Ключевые функции:
 * - Создание отчетов
 * - Просмотр отчетов
 * - Обновление статусов и решений
 * - Фильтрация отчетов
 *
 * Реализует строгий контроль доступа на основе прав пользователей
 */
@Slf4j
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    /**
     * Сервис для работы с отчетами.
     */
    @Autowired
    private ReportService reportService;

    /**
     * Утилита для проверки безопасности и прав доступа.
     */
    @Autowired
    private SecurityUtils securityUtils;

    /**
     * Сервис для работы с пользователями.
     */
    @Autowired
    private UserService userService;

    /**
     * Создание нового отчета.
     *
     * @param report данные нового отчета
     * @return созданный отчет или ошибка
     */
    @PostMapping()
    @PreAuthorize("hasAuthority('CreateReport')")
    public ResponseEntity<ServiceResponse<Report>> createReport(@RequestBody Report report) {
        ServiceResponse<Report> response = reportService.createReport(report);
        return new ResponseEntity<>(response, response.status());
    }

    /**
     * Получение отчета по идентификатору с проверкой прав доступа.
     *
     * @param id идентификатор отчета
     * @return отчет или ошибка доступа
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ViewReport')")
    public ResponseEntity<ServiceResponse<Report>> getReport(@PathVariable Long id) {
        // Получаем отчет
        ServiceResponse<Report> response = reportService.getReport(id);

        // Если отчет не найден, возвращаем текущий ответ
        if (response.data() == null || response.status() != HttpStatus.OK) {
            return new ResponseEntity<>(response, response.status());
        }

        // Проверка прав доступа к отчету
        return checkReportAccess(response);
    }

    /**
     * Получение списка всех доступных отчетов.
     *
     * @return список отчетов с учетом прав пользователя
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ViewReport')")
    public ResponseEntity<ServiceResponse<List<ReportDTO>>> getAllReports() {
        // Если пользователь может видеть все отчеты
        if (securityUtils.isUserInViewAllReportsGroup()) {
            ServiceResponse<List<ReportDTO>> response = reportService.getAllReports();
            return new ResponseEntity<>(response, response.status());
        }

        // Получение отчетов для текущего пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = userService.getUserIdByUsername(username);

        if (userId == null) {
            return new ResponseEntity<>(
                new ServiceResponse<>(null, "Пользователь не найден", HttpStatus.NOT_FOUND),
                HttpStatus.NOT_FOUND
            );
        }

        ServiceResponse<List<ReportDTO>> response = reportService.getReportsByAssignedTo(userId);
        return new ResponseEntity<>(response, response.status());
    }

    /**
     * Обновление отчета с проверкой прав доступа.
     *
     * @param id идентификатор отчета
     * @param report данные для обновления
     * @return обновленный отчет или ошибка
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UpdateReport')")
    public ResponseEntity<ServiceResponse<Report>> updateReport(
        @PathVariable Long id,
        @RequestBody Report report
    ) {
        // Проверка существования отчета
        ServiceResponse<Report> existingReport = reportService.getReport(id);
        if (existingReport.data() == null) {
            return new ResponseEntity<>(existingReport, existingReport.status());
        }

        // Проверка прав доступа
        if (!checkReportUpdateAccess(existingReport)) {
            return new ResponseEntity<>(
                new ServiceResponse <>(null, "Доступ запрещен", HttpStatus.FORBIDDEN),
                HttpStatus.FORBIDDEN
            );
        }

        // Предотвращение изменения назначенного пользователя
        report.setAssignedTo(existingReport.data().getAssignedTo());

        ServiceResponse<Report> response = reportService.updateReport(id, report);
        return new ResponseEntity<>(response, response.status());
    }

    /**
     * Проверка прав доступа к отчету.
     *
     * @param reportResponse ответ с отчетом
     * @return результат проверки доступа
     */
    private ResponseEntity<ServiceResponse<Report>> checkReportAccess(ServiceResponse<Report> reportResponse) {
        // Если пользователь может видеть все отчеты
        if (securityUtils.isUserInViewAllReportsGroup()) {
            return new ResponseEntity<>(reportResponse, reportResponse.status());
        }

        // Получение ID текущего пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = userService.getUserIdByUsername(username);

        // Проверка принадлежности отчета пользователю
        if (userId != null && userId.equals(reportResponse.data().getAssignedTo())) {
            return new ResponseEntity<>(reportResponse, reportResponse.status());
        }

        // Доступ запрещен
        return new ResponseEntity<>(
            new ServiceResponse<>(null, "Доступ запрещен", HttpStatus.FORBIDDEN),
            HttpStatus.FORBIDDEN
        );
    }

    /**
     * Проверка возможности обновления отчета.
     *
     * @param existingReport существующий отчет
     * @return true, если обновление разрешено
     */
    private boolean checkReportUpdateAccess(ServiceResponse<Report> existingReport) {
        // Если пользователь может видеть все отчеты
        if (securityUtils.isUserInViewAllReportsGroup()) {
            return true;
        }

        // Получение ID текущего пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = userService.getUserIdByUsername(username);

        return userId != null && userId.equals(existingReport.data().getAssignedTo());
    }

    /**
     * Обновление решения по отчету.
     *
     * @param id идентификатор отчета
     * @param solution новое решение
     * @return обновленный отчет или ошибка
     */
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

    /**
     * Обновление статуса отчета.
     *
     * @param id идентификатор отчета
     * @param status новый статус
     * @return обновленный отчет или ошибка
     */
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

    /**
     * Удаление отчета.
     *
     * @param id идентификатор отчета
     * @return ответ об успешном удалении или ошибка
     */
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

        if (!checkReportUpdateAccess(existingReport)) {
            return new ResponseEntity<>(
                    new ServiceResponse<>(null, "Доступ запрещен", HttpStatus.FORBIDDEN),
                    HttpStatus.FORBIDDEN
            );
        }

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

        /**
     * Назначение сотрудника для обработки отчета.
     *
     * @param id идентификатор отчета
     * @param assignedTo идентификатор сотрудника
     * @return обновленный отчет или ошибка
     */
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('AssignProcessReport')")
    public ResponseEntity<ServiceResponse<Report>> assignReport(@PathVariable Long id, @RequestParam Long assignedTo) {
        ServiceResponse<Report> response = reportService.assignReport(id, assignedTo);
        return new ResponseEntity<>(response, response.status());
    }

    /**
     * Фильтрация отчетов по различным критериям.
     *
     * @param reporterId идентификатор репортера
     * @param startIncidentDate дата начала инцидента
     * @param endIncidentDate дата окончания инцидента
     * @param incidentLocation место инцидента
     * @param involvedPersons вовлеченные лица
     * @param status статус отчета
     * @param assignedTo идентификатор назначенного сотрудника
     * @return список отфильтрованных отчетов
     */
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