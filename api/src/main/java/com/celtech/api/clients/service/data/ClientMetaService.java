package com.celtech.api.clients.service.data;

import com.celtech.api.clients.auth.AuthPrincipal;
import com.celtech.api.clients.dto.data.ClientMetaDto;
import com.celtech.api.clients.dto.data.ClientMetaUpdate;
import com.celtech.api.clients.model.data.ClientMeta;
import com.celtech.api.clients.model.admin.Site;
import com.celtech.api.clients.repository.data.ClientMetaRepository;
import com.celtech.api.clients.repository.admin.SiteRepository;
import com.celtech.api.clients.service.image.ImageProcessingService;
import com.celtech.api.storage.ImageStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClientMetaService {

    private final ClientMetaRepository metas;
    private final SiteRepository sites;
    private final ImageProcessingService processing;
    private final ImageStore imageStore;

    public ClientMetaService(ClientMetaRepository metas,
                             SiteRepository sites,
                             ImageProcessingService processing,
                             ImageStore imageStore) {
        this.metas = metas;
        this.sites = sites;
        this.processing = processing;
        this.imageStore = imageStore;
    }

    // ---- Public read (authless), by storage slug ----

    public ClientMetaDto getBySlug(String storageSlug) {
        Site site = sites.findByStorageSlug(storageSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown site"));
        ClientMeta meta = metas.findBySiteId(site.getId()).orElseGet(() -> emptyFor(site.getId()));
        return toDto(meta);
    }

    // ---- Portal read (authorized), by siteId ----

    public ClientMetaDto getForSite(AuthPrincipal actor, String siteId) {
        requireManageable(actor, siteId);
        ClientMeta meta = metas.findBySiteId(siteId).orElseGet(() -> emptyFor(siteId));
        return toDto(meta);
    }

    // ---- Portal write (authorized) ----

    public ClientMetaDto update(AuthPrincipal actor, String siteId, ClientMetaUpdate patch) {
        requireManageable(actor, siteId);
        ClientMeta meta = metas.findBySiteId(siteId).orElseGet(() -> emptyFor(siteId));

        if (patch.aboutHeader() != null)       meta.setAboutHeader(patch.aboutHeader());
        if (patch.sections() != null)          meta.setBioSections(patch.sections());
        if (patch.serviceHeader() != null)     meta.setServiceHeader(patch.serviceHeader());
        if (patch.aboutImageCaption() != null) meta.setAboutImageCaption(patch.aboutImageCaption());
        if (patch.aboutImageAltText() != null) meta.setAboutImageAltText(patch.aboutImageAltText());

        return toDto(metas.save(meta));
    }

    public ClientMetaDto uploadAboutImage(AuthPrincipal actor, String siteId, MultipartFile file) {
        Site site = requireManageable(actor, siteId);
        validate(file);

        var stored = processing.process(site.getStorageSlug() + "/about", file);

        ClientMeta meta = metas.findBySiteId(siteId).orElseGet(() -> emptyFor(siteId));
        if (meta.getAboutImageKey() != null) {
            try { imageStore.delete(meta.getAboutImageKey()); } catch (Exception ignore) {}
        }
        meta.setAboutImageKey(stored.imageKey());
        return toDto(metas.save(meta));
    }

    // ---- Guards / helpers ----

    private Site requireManageable(AuthPrincipal actor, String siteId) {
        if (!actor.canManage(siteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your site");
        }
        return sites.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown site"));
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file");
        }
        String ct = file.getContentType();
        if (ct == null || !ct.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Images only");
        }
        if (file.getSize() > 15L * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Max 15MB");
        }
    }

    private ClientMeta emptyFor(String siteId) {
        ClientMeta m = new ClientMeta();
        m.setSiteId(siteId);
        return m;
    }

    private ClientMetaDto toDto(ClientMeta m) {
        return new ClientMetaDto(
                m.getSiteId(),
                m.getAboutHeader(),
                m.getBioSections(),
                m.getServiceHeader(),
                m.getAboutImageKey() == null ? null : imageStore.urlFor(m.getAboutImageKey()),
                m.getAboutImageCaption(),
                m.getAboutImageAltText());
    }
}