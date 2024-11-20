package com.api.AntiCorruptionAPI.Controllers;

import com.api.AntiCorruptionAPI.Models.AccessGroup;
import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import com.api.AntiCorruptionAPI.Services.AccessGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления группами доступа в системе.
 * <p>
 * Ключевые функции:
 * - Получение списка всех групп доступа
 * - Управление правами доступа к группам
 * <p>
 * Требует специального разрешения для доступа к эндпоинтам
 */
@RestController
@RequestMapping("/api/access-groups")
public class AccessGroupController {

    /**
     * Логгер для записи системных событий и ошибок.
     */
    private static final Logger logger = LoggerFactory.getLogger(AccessGroupController.class);

    /**
     * Сервис для работы с группами доступа.
     */
    private final AccessGroupService accessGroupService;

    /**
     * Конструктор для внедрения зависимости сервиса групп доступа.
     *
     * @param accessGroupService сервис для работы с группами доступа
     */
    public AccessGroupController(AccessGroupService accessGroupService) {
        this.accessGroupService = accessGroupService;
    }

    /**
     * Получение списка всех групп доступа.
     * <p>
     * Эндпоинт доступен только пользователям с правом 'ManageUserGroups'.
     *
     * @return ResponseEntity со списком групп доступа или ошибкой
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ManageUserGroups')")
    public ResponseEntity<ServiceResponse<List<AccessGroup>>> getAllAccessGroups() {
        try {
            // Получение списка групп доступа через сервис
            ServiceResponse<List<AccessGroup>> response = accessGroupService.getAllAccessGroups();

            // Возврат успешного ответа
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Логирование системной ошибки
            logger.error("Ошибка при получении групп доступа", e);

            // Формирование ответа об ошибке
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ServiceResponse<>(
                            null,
                            "Не удалось получить группы доступа: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    /**
     * Обработчик для логирования и централизованного управления ошибками.
     *
     * @param ex перехваченное исключение
     * @return ResponseEntity с детализацией ошибки
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServiceResponse<Void>> handleException(Exception ex) {
        logger.error("Необработанная ошибка в контроллере групп доступа", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ServiceResponse<>(
                        null,
                        "Внутренняя ошибка сервера: " + ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));
    }
}