package com.api.AntiCorruptionAPI.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    // Getters and Setters
    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
