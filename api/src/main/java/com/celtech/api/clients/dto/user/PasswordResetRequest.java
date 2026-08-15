package com.celtech.api.clients.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
        @NotBlank @Size(min = 8, message = "Use at least 8 characters") String newPassword) {}