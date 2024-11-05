package com.api.AntiCorruptionAPI.Reponses;

import lombok.Getter;

import java.util.List;

@Getter
public class JwtResponse {
    // Getters
    private String token;
    private Long id;
    private String username;
    private List<String> roles;

    public JwtResponse(String token, Long id, String username, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

}
