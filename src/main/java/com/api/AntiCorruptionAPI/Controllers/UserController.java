package com.api.AntiCorruptionAPI.Controllers;

import com.api.AntiCorruptionAPI.Models.User;
import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import com.api.AntiCorruptionAPI.Requests.AddToGroupRequest;
import com.api.AntiCorruptionAPI.Requests.UserUpdateRequest;
import com.api.AntiCorruptionAPI.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Контроллер для управления пользователями в системе.
 * <p>
 * Ключевые функции:
 * - Добавление пользователей
 * - Удаление пользователей
 * - Обновление информации о пользователях
 * - Управление группами пользователей
 * <p>
 * Реализует строгий контроль доступа через Spring Security
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * Логгер для записи системных событий и ошибок.
     */
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Сервис для работы с пользователями.
     */
    @Autowired
    private UserService userService;

    /**
     * Добавление нового пользователя.
     *
     * @param user данные нового пользователя
     * @return созданный пользователь или ошибка
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('AddUsers')")
    public ResponseEntity<ServiceResponse<User>> addUser(@RequestBody User user) {
        try {
            ServiceResponse<User> response = userService.addUser(user);
            return new ResponseEntity<>(response, response.status());
        } catch (ResponseStatusException e) {
            logger.error("Не удалось добавить пользователя", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(
                            null,
                            e.getReason(),
                            (HttpStatus) e.getStatusCode()
                    ),
                    e.getStatusCode()
            );
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при добавлении пользователя", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(
                            null,
                            "Не удалось добавить пользователя: " + e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Удаление пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return результат удаления
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DeleteUsers')")
    public ResponseEntity<ServiceResponse<Void>> deleteUser(@PathVariable("id") long id) {
        try {
            ServiceResponse<Void> response = userService.deleteUser(id);
            return new ResponseEntity<>(response, response.status());
        } catch (ResponseStatusException e) {
            logger.error("Не удалось удалить пользователя", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(
                            null,
                            e.getReason(),
                            (HttpStatus) e.getStatusCode()
                    ),
                    e.getStatusCode()
            );
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при удалении пользователя", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(
                            null,
                            "Не удалось удалить пользователя: " + e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Обновление информации о пользователе.
     *
     * @param id                идентификатор пользователя
     * @param userUpdateRequest данные для обновления
     * @return обновленный пользователь или ошибка
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('UpdateUsers')")
    public ResponseEntity<ServiceResponse<User>> updateUser(
            @PathVariable("id") long id,
            @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        try {
            ServiceResponse<User> response = userService.updateUser(id, userUpdateRequest);
            return new ResponseEntity<>(response, response.status());
        } catch (ResponseStatusException e) {
            logger.error("Не удалось обновить пользователя", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(
                            null,
                            e.getReason(),
                            (HttpStatus) e.getStatusCode()
                    ),
                    e.getStatusCode()
            );
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при обновлении пользователя", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(
                            null,
                            "Не удалось обновить пользователя: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Добавление пользователя в группу.
     *
     * @param request данные для добавления в группу
     * @return обновленный пользователь или ошибка
     */
    @PostMapping("/add-to-group")
    @PreAuthorize("hasAuthority('ManageUserGroups')")
    public ResponseEntity<ServiceResponse<User>> addUserToGroup(
            @RequestBody AddToGroupRequest request
    ) {
        try {
            ServiceResponse<User> response = userService.addUserToGroup(
                    request.getUserId(),
                    request.getGroupId()
            );
            return new ResponseEntity<>(response, response.status());
        } catch (ResponseStatusException e) {
            logger.error("Не удалось добавить пользователя в группу", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(
                            null,
                            e.getReason(),
                            (HttpStatus) e.getStatusCode()
                    ),
                    e.getStatusCode()
            );
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при добавлении пользователя в группу", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(
                            null,
                            "Не удалось добавить пользователя в группу: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Получение списка всех пользователей.
     *
     * @return список пользователей или ошибка
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ManageUserGroups')")
    public ResponseEntity<ServiceResponse<List<User>>> getAllUsers() {
        try {
            ServiceResponse<List<User>> response = userService.getAllUsers();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при получении пользователей", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ServiceResponse<>(
                            null,
                            "Произошла ошибка при получении пользователей",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    /**
     * Удаление пользователя из группы.
     *
     * @param request данные для удаления из группы
     * @return обновленный пользователь или ошибка
     */
    @DeleteMapping("/remove-from-group")
    @PreAuthorize("hasAuthority('ManageUser Groups')")
    public ResponseEntity<ServiceResponse<User>> removeUserFromGroup(
            @RequestBody AddToGroupRequest request) {
        try {
            ServiceResponse<User> response = userService.removeUserFromGroup(
                    request.getUserId(),
                    request.getGroupId()
            );
            return new ResponseEntity<>(response, response.status());
        } catch (ResponseStatusException e) {
            logger.error("Не удалось удалить пользователя из группы", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(
                            null,
                            e.getReason(),
                            (HttpStatus) e.getStatusCode()
                    ),
                    e.getStatusCode()
            );
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при удалении пользователя из группы", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(
                            null,
                            "Не удалось удалить пользователя из группы: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Получение пользователей в группе "SolveReport".
     *
     * @return список пользователей или ошибка
     */
    @GetMapping("/get-agents")
    @PreAuthorize("hasAuthority('AssignProcessReport')")
    public ResponseEntity<ServiceResponse<List<User>>> getUsersInSolveReportGroup() {
        try {
            ServiceResponse<List<User>> response = userService.getUsersByGroup("SolveReport");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при получении пользователей в группе SolveReport", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ServiceResponse<>(
                            null,
                            "Произошла ошибка при получении пользователей",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
}