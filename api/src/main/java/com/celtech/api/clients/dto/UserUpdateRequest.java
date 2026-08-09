package com.celtech.api.clients.dto;

import java.util.List;

/** All optional — only provided fields change. Password reset is a separate endpoint. */
public record UserUpdateRequest(
        String displayName,
        String role,
        List<String> siteIds,
        Boolean enabled) {}