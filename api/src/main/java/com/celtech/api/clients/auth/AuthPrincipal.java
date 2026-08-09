package com.celtech.api.clients.auth;

import java.util.List;

/**
 * The authenticated portal user, extracted from the JWT. Carries the site
 * ownership set used for the multi-tenant authorization check.
 */
public record AuthPrincipal(
        String userId,
        String email,
        String role,
        List<String> siteIds) {

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /** The core multi-tenant rule: admins pass; clients must own the site. */
    public boolean canManage(String siteId) {
        return isAdmin() || (siteIds != null && siteIds.contains(siteId));
    }
}
