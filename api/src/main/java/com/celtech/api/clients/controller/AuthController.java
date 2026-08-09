package com.celtech.api.clients.controller;

import com.celtech.api.clients.auth.JwtCookieFilter;
import com.celtech.api.clients.auth.JwtService;
import com.celtech.api.clients.dto.LoginRequest;
import com.celtech.api.clients.dto.MeResponse;
import com.celtech.api.clients.model.ClientUser;
import com.celtech.api.clients.model.Site;
import com.celtech.api.clients.repository.ClientUserRepository;
import com.celtech.api.clients.repository.SiteRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/portal/auth")
public class AuthController {

    private final ClientUserRepository users;
    private final SiteRepository sites;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final boolean cookieSecure;

    public AuthController(ClientUserRepository users,
                          SiteRepository sites,
                          PasswordEncoder encoder,
                          JwtService jwt,
                          @Value("${app.auth.cookie-secure}") boolean cookieSecure) {
        this.users = users;
        this.sites = sites;
        this.encoder = encoder;
        this.jwt = jwt;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/login")
    public ResponseEntity<MeResponse> login(@Valid @RequestBody LoginRequest req) {
        ClientUser user = users.findByEmailIgnoreCase(req.email())
                .filter(ClientUser::isEnabled)
                .filter(u -> encoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials"));

        String token = jwt.issue(user);
        ResponseCookie cookie = buildCookie(token, jwt.ttlSeconds());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(me(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cleared = buildCookie("", 0);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cleared.toString())
                .build();
    }

    private MeResponse me(ClientUser user) {
        List<Site> owned = user.getSiteIds() == null ? List.of()
                : sites.findAllById(user.getSiteIds());
        var summaries = owned.stream()
                .map(s -> new MeResponse.SiteSummary(s.getId(), s.getName(), s.getStorageSlug()))
                .toList();
        return new MeResponse(user.getId(), user.getEmail(), user.getDisplayName(),
                user.getRole(), summaries);
    }

    private ResponseCookie buildCookie(String value, long maxAgeSeconds) {
        return ResponseCookie.from(JwtCookieFilter.COOKIE_NAME, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }
}