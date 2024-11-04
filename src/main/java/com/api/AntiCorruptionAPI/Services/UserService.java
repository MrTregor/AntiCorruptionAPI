package com.api.AntiCorruptionAPI.Services;

import com.api.AntiCorruptionAPI.Models.User;
import com.api.AntiCorruptionAPI.Reponses.ServiceResponse;
import com.api.AntiCorruptionAPI.Repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    // Метод для добавления пользователя
    public ServiceResponse<User> addUser (User user) {
        try {
            // Проверка на уникальность логина
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                logger.warn("Attempt to add user with existing username: {}", user.getUsername());
                return new ServiceResponse<>(null, "Username already exists", HttpStatus.CONFLICT);
            }

            User savedUser  = userRepository.save(user);
            logger.info("User  added successfully: {}", savedUser .getUsername());
            return new ServiceResponse<>(savedUser , "User  added successfully", HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            logger.error("Failed to add user due to data integrity violation", e);
            return new ServiceResponse<>(null, "Invalid user data", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error while adding user", e);
            return new ServiceResponse<>(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Метод для удаления пользователя
    public ServiceResponse<Void> deleteUser (long id) {
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                userRepository.deleteById(id);
                logger.info("User  deleted successfully: {}", id);
                return new ServiceResponse<>(null, "User  deleted successfully", HttpStatus.NO_CONTENT);
            } else {
                logger.warn("Attempt to delete non-existent user: {}", id);
                return new ServiceResponse<>(null, "User  not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Unexpected error while deleting user", e);
            return new ServiceResponse<>(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}