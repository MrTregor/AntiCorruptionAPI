package com.api.AntiCorruptionAPI.Controllers;

import com.api.AntiCorruptionAPI.Components.JwtUtils;
import com.api.AntiCorruptionAPI.Models.User;
import com.api.AntiCorruptionAPI.Requests.UpdatePasswordRequest;
import com.api.AntiCorruptionAPI.Responses.JwtResponse;
import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import com.api.AntiCorruptionAPI.Requests.LoginRequest;
import com.api.AntiCorruptionAPI.Requests.RegisterRequest;
import com.api.AntiCorruptionAPI.Components.UserDetailsImpl;
import com.api.AntiCorruptionAPI.Services.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<ServiceResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername());
            return ResponseEntity.ok(new ServiceResponse<>(jwtResponse, "Authentication successful.", HttpStatus.OK));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>(null, "Invalid username or password.", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ServiceResponse<>(null, "Authentication failed. " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('AddUsers')")
    public ResponseEntity<ServiceResponse<String>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User newUser = userDetailsService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());
            return ResponseEntity.ok(new ServiceResponse<>("User  registered successfully: " + newUser.getUsername(), null, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ServiceResponse<>(null, "Error: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PutMapping("/update-password/{user_id}")
    @PreAuthorize("hasAuthority('UpdateUsers')") // или другое подходящее разрешение
    public ResponseEntity<ServiceResponse<String>> updateUserPassword(
            @PathVariable("user_id") Long userId,
            @RequestBody UpdatePasswordRequest updatePasswordRequest
    ) {
        try {
            ServiceResponse<String> response = userDetailsService.updateUserPassword(userId, updatePasswordRequest.getNewPassword());
            return new ResponseEntity<>(response, response.status());
        } catch (ResponseStatusException e) {
            logger.error("Failed to update user password", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(null, e.getReason(), (HttpStatus) e.getStatusCode()),
                    e.getStatusCode()
            );
        } catch (Exception e) {
            logger.error("Unexpected error while updating user password", e);
            return new ResponseEntity<>(
                    new ServiceResponse<>(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}