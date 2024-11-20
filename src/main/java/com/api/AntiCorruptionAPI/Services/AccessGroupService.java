package com.api.AntiCorruptionAPI.Services;

import com.api.AntiCorruptionAPI.Models.AccessGroup;
import com.api.AntiCorruptionAPI.Repositories.AccessGroupRepository;
import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления группами доступа в системе.
 * <p>
 * Предоставляет функционал для работы с группами доступа,
 * включая получение списка и выполнение операций с ними.
 */
@Service
public class AccessGroupService {

    /**
     * Репозиторий для взаимодействия с группами доступа в базе данных.
     */
    private final AccessGroupRepository accessGroupRepository;

    /**
     * Конструктор для внедрения зависимости репозитория групп доступа.
     *
     * @param accessGroupRepository Репозиторий для работы с группами доступа
     */
    public AccessGroupService(AccessGroupRepository accessGroupRepository) {
        this.accessGroupRepository = accessGroupRepository;
    }

    /**
     * Получает список всех групп доступа в системе.
     *
     * @return {@link ServiceResponse} со списком групп доступа
     */
    public ServiceResponse<List<AccessGroup>> getAllAccessGroups() {
        // Получение всех групп доступа из репозитория
        List<AccessGroup> accessGroups = accessGroupRepository.findAll();

        // Формирование ответа с учетом результата
        return new ServiceResponse<>(
                accessGroups,
                accessGroups.isEmpty()
                        ? "No access groups found"
                        : "Access groups retrieved successfully",
                accessGroups.isEmpty()
                        ? HttpStatus.NO_CONTENT
                        : HttpStatus.OK
        );
    }

    /**
     * Получает группу доступа по её уникальному идентификатору.
     *
     * @param id Идентификатор группы доступа
     * @return {@link ServiceResponse} с найденной группой доступа
     */
    public ServiceResponse<AccessGroup> getAccessGroupById(Long id) {
        Optional<AccessGroup> accessGroup = accessGroupRepository.findById(id);

        return accessGroup.map(group -> new ServiceResponse<>(
                group,
                "Access group found successfully",
                HttpStatus.OK
        )).orElse(new ServiceResponse<>(
                null,
                "Access group not found with id: " + id,
                HttpStatus.NOT_FOUND
        ));
    }

    /**
     * Создает новую группу доступа.
     *
     * @param accessGroup Группа доступа для создания
     * @return {@link ServiceResponse} с созданной группой доступа
     */
    public ServiceResponse<AccessGroup> createAccessGroup(AccessGroup accessGroup) {
        try {
            AccessGroup savedAccessGroup = accessGroupRepository.save(accessGroup);
            return new ServiceResponse<>(
                    savedAccessGroup,
                    "Access group created successfully",
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ServiceResponse<>(
                    null,
                    "Failed to create access group: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Удаляет группу доступа по её идентификатору.
     *
     * @param id Идентификатор группы доступа для удаления
     * @return {@link ServiceResponse} с результатом операции
     */
    public ServiceResponse<Void> deleteAccessGroup(Long id) {
        if (accessGroupRepository.existsById(id)) {
            accessGroupRepository.deleteById(id);
            return new ServiceResponse<>(
                    null,
                    "Access group deleted successfully",
                    HttpStatus.OK
            );
        } else {
            return new ServiceResponse<>(
                    null,
                    "Access group not found with id: " + id,
                    HttpStatus.NOT_FOUND
            );
        }
    }
}