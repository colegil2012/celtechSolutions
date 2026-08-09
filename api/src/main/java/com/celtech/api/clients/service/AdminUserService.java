package com.celtech.api.clients.service;

import com.celtech.api.clients.auth.AuthPrincipal;
import com.celtech.api.clients.dto.UserCreateRequest;
import com.celtech.api.clients.dto.UserDto;
import com.celtech.api.clients.dto.UserUpdateRequest;
import com.celtech.api.clients.model.ClientUser;
import com.celtech.api.clients.repository.ClientUserRepository;
import com.celtech.api.clients.repository.SiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class AdminUserService {

    private static final Logger log =
            LoggerFactory.getLogger(AdminUserService.class);

    private final ClientUserRepository users;
    private final SiteRepository sites;
    private final PasswordEncoder encoder;

    public AdminUserService(ClientUserRepository users, SiteRepository sites, PasswordEncoder encoder) {
        this.users = users;
        this.sites = sites;
        this.encoder = encoder;
    }

    public List<UserDto> list() {
        return users.findAll().stream().map(this::toDto).toList();
    }

    public UserDto create(UserCreateRequest req) {
        users.findByEmailIgnoreCase(req.email()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        });
        validateSiteIds(req.siteIds());

        ClientUser u = new ClientUser();
        u.setEmail(req.email().trim().toLowerCase(Locale.ROOT));
        u.setDisplayName(req.displayName());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole(normalizeRole(req.role()));
        u.setSiteIds(req.siteIds() != null ? req.siteIds() : List.of());
        u.setEnabled(true);
        ClientUser saved = users.save(u);
        log.info("[admin] created user {} role={} sites={}",
                saved.getEmail(), saved.getRole(), saved.getSiteIds());
        return toDto(saved);
    }

    public UserDto update(String userId, UserUpdateRequest req) {
        ClientUser u = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such user"));
        if (req.displayName() != null) u.setDisplayName(req.displayName());
        if (req.role() != null)        u.setRole(normalizeRole(req.role()));
        if (req.siteIds() != null)     { validateSiteIds(req.siteIds()); u.setSiteIds(req.siteIds()); }
        if (req.enabled() != null)     u.setEnabled(req.enabled());
        return toDto(users.save(u));
    }

    public void resetPassword(String userId, String newPassword) {
        ClientUser u = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such user"));
        u.setPasswordHash(encoder.encode(newPassword));
        users.save(u);
    }

    /** Guard: an admin can't disable or demote the last remaining admin. */
    public void guardLastAdmin(AuthPrincipal actor, String targetUserId, UserUpdateRequest req) {
        boolean demoting = "CLIENT".equalsIgnoreCase(String.valueOf(req.role()));
        boolean disabling = Boolean.FALSE.equals(req.enabled());
        if (!demoting && !disabling) return;

        ClientUser target = users.findById(targetUserId).orElse(null);
        if (target == null || !"ADMIN".equals(target.getRole())) return;

        long activeAdmins = users.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole()) && u.isEnabled())
                .count();
        if (activeAdmins <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot disable or demote the last active admin");
        }
    }

    private void validateSiteIds(List<String> siteIds) {
        if (siteIds == null) return;
        for (String id : siteIds) {
            if (sites.findById(id).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown site id: " + id);
            }
        }
    }

    private String normalizeRole(String role) {
        if (role == null) return "CLIENT";
        return "ADMIN".equalsIgnoreCase(role) ? "ADMIN" : "CLIENT";
    }

    private UserDto toDto(ClientUser u) {
        return new UserDto(u.getId(), u.getEmail(), u.getDisplayName(), u.getRole(),
                u.getSiteIds(), u.isEnabled(), u.getCreatedAt());
    }
}