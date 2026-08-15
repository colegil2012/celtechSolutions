package com.celtech.api.clients.dto.admin;

import java.time.Instant;
import java.util.List;

/** Never exposes the password hash. */
public record UserDto(
        String id,
        String email,
        String displayName,
        String role,
        List<String> siteIds,
        boolean enabled,
        Instant createdAt) {}