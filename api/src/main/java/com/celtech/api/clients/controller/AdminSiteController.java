package com.celtech.api.clients.controller;

import com.celtech.api.clients.dto.SiteCreateRequest;
import com.celtech.api.clients.dto.SiteDto;
import com.celtech.api.clients.dto.SiteUpdateRequest;
import com.celtech.api.clients.service.AdminSiteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only site management. Secured by SecurityConfig:
 *   .requestMatchers("/api/portal/admin/**").hasRole("ADMIN")
 * so no per-method role check is needed here.
 */
@RestController
@RequestMapping("/api/portal/admin/sites")
public class AdminSiteController {

    private final AdminSiteService svc;

    public AdminSiteController(AdminSiteService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<SiteDto> list() {
        return svc.list();
    }

    @PostMapping
    public SiteDto create(@Valid @RequestBody SiteCreateRequest req) {
        return svc.create(req);
    }

    @PutMapping("/{siteId}")
    public SiteDto update(@PathVariable String siteId, @RequestBody SiteUpdateRequest req) {
        return svc.update(siteId, req);
    }
}