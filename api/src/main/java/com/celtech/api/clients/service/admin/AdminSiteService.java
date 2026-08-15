package com.celtech.api.clients.service.admin;

import com.celtech.api.clients.dto.admin.SiteCreateRequest;
import com.celtech.api.clients.dto.admin.SiteDto;
import com.celtech.api.clients.dto.admin.SiteUpdateRequest;
import com.celtech.api.clients.model.admin.Site;
import com.celtech.api.clients.model.admin.SiteConfig;
import com.celtech.api.clients.repository.admin.SiteRepository;
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
        s.setConfig(SiteConfig.defaults());
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
        if (req.config() != null) s.setConfig(sanitize(req.config()));
        return toDto(sites.save(s));
    }

    /** Keep counts within sane bounds so bad input can't warp the portal UI. */
    private SiteConfig sanitize(SiteConfig c) {
        int bios = Math.clamp(c.getBioSectionCount(), 0, 10);
        int svc  = Math.clamp(c.getServiceHeaderCount(), 0, 12);
        return SiteConfig.builder()
                .bioSectionCount(bios)
                .serviceHeadersEnabled(c.isServiceHeadersEnabled())
                .serviceHeaderCount(c.isServiceHeadersEnabled() ? svc : 0)
                .aboutImageEnabled(c.isAboutImageEnabled())
                .albumsEnabled(c.isAlbumsEnabled())
                .build();
    }

    private SiteDto toDto(Site s) {
        return new SiteDto(s.getId(), s.getName(), s.getStorageSlug(), s.isEnabled(), s.getNotifyEmail(), s.getConfig(),s.getCreatedAt());
    }
}