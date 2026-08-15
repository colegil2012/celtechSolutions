package com.celtech.api.clients.dto.user;

import com.celtech.api.clients.model.admin.SiteConfig;

import java.util.List;

public record MeResponse(
        String userId,
        String email,
        String displayName,
        String role,
        List<SiteSummary> sites) {

    public record SiteSummary(String id, String name, String storageSlug, SiteConfig config) {}
}
