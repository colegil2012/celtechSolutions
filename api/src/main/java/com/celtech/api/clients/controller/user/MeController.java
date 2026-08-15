package com.celtech.api.clients.controller.user;

import com.celtech.api.clients.auth.AuthPrincipal;
import com.celtech.api.clients.dto.user.MeResponse;
import com.celtech.api.clients.dto.user.PasswordChangeRequest;
import com.celtech.api.clients.model.user.ClientUser;
import com.celtech.api.clients.model.admin.Site;
import com.celtech.api.clients.repository.user.ClientUserRepository;
import com.celtech.api.clients.repository.admin.SiteRepository;
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
                .map(s -> new MeResponse.SiteSummary(s.getId(), s.getName(), s.getStorageSlug(), s.getConfig()))
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