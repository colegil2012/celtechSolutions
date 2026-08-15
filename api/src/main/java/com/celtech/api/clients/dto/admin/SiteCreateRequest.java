package com.celtech.api.clients.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SiteCreateRequest(
        @NotBlank String name,
        /** Storage slug = Spaces folder + public URL segment. Lowercase, dash-safe. */
        @NotBlank
        @Pattern(regexp = "^[a-z0-9][a-z0-9-]*$",
                message = "Use lowercase letters, numbers, and dashes only")
        String storageSlug,
        @Email String notifyEmail) {}