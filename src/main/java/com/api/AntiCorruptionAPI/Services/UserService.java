package com.api.AntiCorruptionAPI.Services;

import com.api.AntiCorruptionAPI.Models.AccessGroup;
import com.api.AntiCorruptionAPI.Models.User;
import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import com.api.AntiCorruptionAPI.Repositories.AccessGroupRepository;
import com.api.AntiCorruptionAPI.Repositories.UserRepository;
import com.api.AntiCorruptionAPI.Requests.UserUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final AccessGroupRepository accessGroupRepository;

    public UserService(UserRepository userRepository, AccessGroupRepository accessGroupRepository) {
        this.userRepository = userRepository;
        this.accessGroupRepository = accessGroupRepository;
    }

    public ServiceResponse<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ServiceResponse<>(
                users,
                "Users retrieved successfully",
                HttpStatus.OK
        );
    }

    // Метод для добавления пользователя
    public ServiceResponse<User> addUser(User user) {
        try {
            // Проверка на уникальность логина
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                logger.warn("Attempt to add user with existing username: {}", user.getUsername());
                return new ServiceResponse<>(null, "Username already exists", HttpStatus.CONFLICT);
            }

            User savedUser = userRepository.save(user);
            logger.info("User  added successfully: {}", savedUser.getUsername());
            return new ServiceResponse<>(savedUser, "User  added successfully", HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            logger.error("Failed to add user due to data integrity violation", e);
            return new ServiceResponse<>(null, "Invalid user data", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error while adding user", e);
            return new ServiceResponse<>(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Метод для удаления пользователя
    public ServiceResponse<Void> deleteUser(long id) {
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                userRepository.deleteById(id);
                logger.info("User  deleted successfully: {}", id);
                return new ServiceResponse<>(null, "User  deleted successfully", HttpStatus.OK);
            } else {
                logger.warn("Attempt to delete non-existent user: {}", id);
                return new ServiceResponse<>(null, "User  not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Unexpected error while deleting user", e);
            return new ServiceResponse<>(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ServiceResponse<List<User>> getUsersByGroup(String groupName) {
        List<User> users = userRepository.findByGroupsName(groupName);
        return new ServiceResponse<>(
                users,
                "Users retrieved successfully",
                HttpStatus.OK
        );
    }
    public ServiceResponse<User> updateUser(long id, UserUpdateRequest userUpdateRequest) {
        try {
            // Найти пользователя по ID
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User  not found"));

            Class<?> requestClass = userUpdateRequest.getClass();
            Class<?> userClass = user.getClass();

            for (Method getter : requestClass.getMethods()) {
                if (getter.getName().startsWith("get") && !getter.getName().equals("getClass")) {
                    try {
                        Object value = getter.invoke(userUpdateRequest);
                        if (value != null) {
                            String setterName = "set" + getter.getName().substring(3);
                            Method setter = userClass.getMethod(setterName, getter.getReturnType());
                            setter.invoke(user, value);
                        }
                    } catch (Exception e) {
                        logger.error("Error updating user field: {}", getter.getName(), e);
                    }
                }
            }

            // Обновление полей пользователя
            if (userUpdateRequest.getUsername() != null) {
                user.setUsername(userUpdateRequest.getUsername());
            }

            // Обработка обновления групп
            if (userUpdateRequest.getGroups() != null) {
                // Очистить существующие группы
                user.getGroups().clear();

                // Добавить новые группы
                for (AccessGroup group : userUpdateRequest.getGroups()) {
                    // Найти группу по ID (предполагается, что у вас есть метод в репозитории)
                    AccessGroup existingGroup = accessGroupRepository.findById(group.getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found with ID: " + group.getId()));
                    user.getGroups().add(existingGroup);
                }
            }

            // Сохранить обновленного пользователя
            User updatedUser = userRepository.save(user);
            return new ServiceResponse<>(updatedUser, "User  updated successfully", HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ServiceResponse<>(null, e.getReason(), (HttpStatus) e.getStatusCode());
        } catch (Exception e) {
            logger.error("Unexpected error while updating user", e);
            return new ServiceResponse<>(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse<User> addUserToGroup(Long userId, Long groupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        AccessGroup group = accessGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Access group not found"));

        user.getGroups().add(group);
        return new ServiceResponse<>(null, "User added to group successfully", HttpStatus.OK);
    }

    public Long getUserIdByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::getId).orElse(null);
    }

    public ServiceResponse<User> removeUserFromGroup(Long userId, Long groupId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "User not found"));

            AccessGroup group = accessGroupRepository.findById(groupId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Group not found"));

            // Удаление группы из множества групп пользователя
            user.getGroups().remove(group);

            User updatedUser = userRepository.save(user);

            return new ServiceResponse<>(
                    updatedUser,
                    "User removed from group successfully",
                    HttpStatus.OK
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error removing user from group"
            );
        }
    }
}