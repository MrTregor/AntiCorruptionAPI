package com.api.AntiCorruptionAPI.Reponses;

import java.util.List;

/**
 * @param token Getters
 */
public record JwtResponse(String token, Long id, String username, List<String> roles) {

}
