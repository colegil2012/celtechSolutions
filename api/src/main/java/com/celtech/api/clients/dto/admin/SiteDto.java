package com.celtech.api.clients.dto.admin;

import com.celtech.api.clients.model.admin.SiteConfig;

import java.time.Instant;

public record SiteDto(
        String id,
        String name,
        String storageSlug,
        boolean enabled,
        String notifyEmail,
        SiteConfig config,
        Instant createdAt) {}