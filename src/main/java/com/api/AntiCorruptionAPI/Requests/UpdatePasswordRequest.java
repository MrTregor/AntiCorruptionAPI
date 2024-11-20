package com.api.AntiCorruptionAPI.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdatePasswordRequest {
    @NotBlank
    private String newPassword;

}
