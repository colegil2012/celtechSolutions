package com.celtech.api.clients.dto;

/** Only name/enabled are editable. storageSlug is immutable once created —
 *  changing it would orphan every Spaces object and gallery reference. */
public record SiteUpdateRequest(
        String name,
        Boolean enabled,
        String notifyEmail) {}