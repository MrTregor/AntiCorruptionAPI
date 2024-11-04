package com.api.AntiCorruptionAPI.Controllers;

import com.api.AntiCorruptionAPI.JwtUtils;
import com.api.AntiCorruptionAPI.Models.User;
import com.api.AntiCorruptionAPI.Reponses.JwtResponse;
import com.api.AntiCorruptionAPI.Requests.LoginRequest;
import com.api.AntiCorruptionAPI.Requests.RegisterRequest;
import com.api.AntiCorruptionAPI.UserDetailsImpl;
import com.api.AntiCorruptionAPI.Services.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.GrantedAuthority;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Добавьте это поле

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser (@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles);
            return ResponseEntity.ok(jwtResponse);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed. "+e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser (@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User newUser = userDetailsService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());
            return ResponseEntity.ok("User registered successfully: " + newUser.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}