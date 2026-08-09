package com.celtech.api.clients.service;

import com.celtech.api.clients.dto.SiteCreateRequest;
import com.celtech.api.clients.dto.SiteDto;
import com.celtech.api.clients.dto.SiteUpdateRequest;
import com.celtech.api.clients.model.Site;
import com.celtech.api.clients.repository.SiteRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminSiteService {

    private final SiteRepository sites;

    public AdminSiteService(SiteRepository sites) {
        this.sites = sites;
    }

    public List<SiteDto> list() {
        return sites.findAll().stream().map(this::toDto).toList();
    }

    public SiteDto create(SiteCreateRequest req) {
        sites.findByStorageSlug(req.storageSlug()).ifPresent(s -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slug already in use");
        });
        Site s = new Site();
        s.setName(req.name().trim());
        s.setStorageSlug(req.storageSlug());
        s.setNotifyEmail(req.notifyEmail());
        s.setEnabled(true);
        try {
            return toDto(sites.save(s));
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slug already in use");
        }
    }

    public SiteDto update(String siteId, SiteUpdateRequest req) {
        Site s = sites.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such site"));
        if (req.name() != null && !req.name().isBlank()) s.setName(req.name().trim());
        if (req.enabled() != null) s.setEnabled(req.enabled());
        if (req.notifyEmail() != null) s.setNotifyEmail(req.notifyEmail());
        return toDto(sites.save(s));
    }

    private SiteDto toDto(Site s) {
        return new SiteDto(s.getId(), s.getName(), s.getStorageSlug(), s.isEnabled(), s.getNotifyEmail(), s.getCreatedAt());
    }
}