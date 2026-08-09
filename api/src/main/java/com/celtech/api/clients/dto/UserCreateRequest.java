package com.celtech.api.clients.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserCreateRequest(
        @Email @NotBlank String email,
        String displayName,
        @NotBlank @Size(min = 8, message = "Use at least 8 characters") String password,
        String role,                 // "CLIENT" | "ADMIN"; defaults CLIENT
        List<String> siteIds) {}