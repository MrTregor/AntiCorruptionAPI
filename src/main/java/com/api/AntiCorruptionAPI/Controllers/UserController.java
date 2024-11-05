package com.api.AntiCorruptionAPI.Controllers;

import com.api.AntiCorruptionAPI.Models.User;
import com.api.AntiCorruptionAPI.Reponses.ServiceResponse;
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

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('AddUsers')")
    public ResponseEntity<ServiceResponse<User>> addUser(@RequestBody User user) {
        try {
            ServiceResponse<User> response = userService.addUser (user);
            return new ResponseEntity<>(response, response.status());
        } catch (ResponseStatusException e) {
            logger.error("Failed to add user", e);
            return new ResponseEntity<>(null, e.getStatusCode());
        } catch (Exception e) {
            logger.error("Unexpected error while adding user", e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DeleteUsers')")  // Добавлено разрешение на удаление
    public ResponseEntity<ServiceResponse<Void>> deleteUser(@PathVariable("id") long id) {
        try {
            ServiceResponse<Void> response = userService.deleteUser(id);
            return new ResponseEntity<>(response, response.status());
        } catch (ResponseStatusException e) {
            logger.error("Failed to delete user", e);
            return new ResponseEntity<>(e.getStatusCode());
        } catch (Exception e) {
            logger.error("Unexpected error while deleting user", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('UpdateUsers')")
    public ResponseEntity<ServiceResponse<User>> updateUser(@PathVariable("id") long id, @RequestBody UserUpdateRequest userUpdateRequest) {
        try {
            ServiceResponse<User> response = userService.updateUser(id, userUpdateRequest);
            return new ResponseEntity<>(response, response.status());
        } catch (ResponseStatusException e) {
            logger.error("Failed to update user", e);
            return new ResponseEntity<>(new ServiceResponse<>(null, e.getReason(), (HttpStatus) e.getStatusCode()), e.getStatusCode());
        } catch (Exception e) {
            logger.error("Unexpected error while updating user", e);
            return new ResponseEntity<>(new ServiceResponse<>(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-to-group")
    @PreAuthorize("hasAuthority('ManageUserGroups')")
    public ResponseEntity<ServiceResponse<User>> addUserToGroup(@RequestBody AddToGroupRequest request) {
        try {
            ServiceResponse<User> response = userService.addUserToGroup(request.getUserId(), request.getGroupId());
            return new ResponseEntity<>(response, response.status());
        } catch (ResponseStatusException e) {
            logger.error("Failed to add user to group", e);
            return new ResponseEntity<>(new ServiceResponse<>(null, e.getReason(), (HttpStatus) e.getStatusCode()), e.getStatusCode());
        } catch (Exception e) {
            logger.error("Unexpected error while adding user to group", e);
            return new ResponseEntity<>(new ServiceResponse<>(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}