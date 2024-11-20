package com.api.AntiCorruptionAPI.Responses;

import org.springframework.http.HttpStatus;

public record ServiceResponse<T>(T data, String message, HttpStatus status) {

}