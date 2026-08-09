package com.celtech.api.clients.controller;

import com.celtech.api.clients.auth.AuthPrincipal;
import com.celtech.api.clients.dto.MeResponse;
import com.celtech.api.clients.dto.PasswordChangeRequest;
import com.celtech.api.clients.model.ClientUser;
import com.celtech.api.clients.model.Site;
import com.celtech.api.clients.repository.ClientUserRepository;
import com.celtech.api.clients.repository.SiteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/portal")
public class MeController {

    private final ClientUserRepository users;
    private final SiteRepository sites;
    private final PasswordEncoder encoder;

    public MeController(ClientUserRepository users, SiteRepository sites, PasswordEncoder encoder) {
        this.users = users;
        this.sites = sites;
        this.encoder = encoder;
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal AuthPrincipal actor) {
        ClientUser user = users.findById(actor.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        List<Site> owned = user.getSiteIds() == null ? List.of()
                : sites.findAllById(user.getSiteIds());
        var summaries = owned.stream()
                .map(s -> new MeResponse.SiteSummary(s.getId(), s.getName(), s.getStorageSlug()))
                .toList();
        return new MeResponse(user.getId(), user.getEmail(), user.getDisplayName(),
                user.getRole(), summaries);
    }

    @PostMapping("/me/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal AuthPrincipal actor,
                                               @Valid @RequestBody PasswordChangeRequest req) {
        ClientUser user = users.findById(actor.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!encoder.matches(req.currentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Current password is incorrect");
        }
        user.setPasswordHash(encoder.encode(req.newPassword()));
        users.save(user);
        return ResponseEntity.noContent().build();
    }
}