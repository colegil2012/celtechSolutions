package com.celtech.api.clients.dto;

import java.time.Instant;

public record SiteDto(
        String id,
        String name,
        String storageSlug,
        boolean enabled,
        String notifyEmail,
        Instant createdAt) {}