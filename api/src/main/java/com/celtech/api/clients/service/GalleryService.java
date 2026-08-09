package com.celtech.api.clients.service;


import com.celtech.api.clients.auth.AuthPrincipal;
import com.celtech.api.clients.dto.GalleryImageDto;
import com.celtech.api.clients.dto.GalleryImageUpdate;
import com.celtech.api.clients.dto.GalleryResponse;
import com.celtech.api.clients.model.GalleryImage;
import com.celtech.api.clients.model.Site;
import com.celtech.api.clients.repository.GalleryImageRepository;
import com.celtech.api.clients.repository.SiteRepository;
import com.celtech.api.storage.ImageStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GalleryService {

    private final GalleryImageRepository gallery;
    private final SiteRepository sites;
    private final ImageProcessingService processing;
    private final ImageStore imageStore;
    private final GalleryTagService tagService;

    public GalleryService(GalleryImageRepository gallery,
                          SiteRepository sites,
                          ImageProcessingService processing,
                          ImageStore imageStore,
                          GalleryTagService tagService) {
        this.gallery = gallery;
        this.sites = sites;
        this.processing = processing;
        this.imageStore = imageStore;
        this.tagService = tagService;
    }

    // ---- Public read (authless): tags + images in one payload ----

    public GalleryResponse listBySlug(String storageSlug) {
        Site site = sites.findByStorageSlug(storageSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown site"));
        var imgs = gallery.findBySiteIdOrderByPositionAsc(site.getId())
                .stream().map(this::toDto).toList();
        var tags = tagService.listForSite(site.getId());
        return new GalleryResponse(tags, imgs);
    }

    // ---- Portal writes (authorized) ----

    public GalleryImageDto upload(AuthPrincipal actor, String siteId, MultipartFile file) {
        Site site = requireManageable(actor, siteId);
        validate(file);

        var stored = processing.process(site.getStorageSlug(), file);

        GalleryImage img = new GalleryImage();
        img.setSiteId(siteId);
        img.setImageKey(stored.imageKey());
        img.setThumbKey(stored.thumbKey());
        img.setLqip(stored.lqip());
        img.setUploadedBy(actor.userId());
        img.setPosition(gallery.findBySiteIdOrderByPositionAsc(siteId).size());

        return toDto(gallery.save(img));
    }

    public GalleryImageDto update(AuthPrincipal actor, String imageId, GalleryImageUpdate patch) {
        GalleryImage img = gallery.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such image"));
        requireManageable(actor, img.getSiteId());

        if (patch.caption() != null) img.setCaption(patch.caption());
        if (patch.altText() != null) img.setAltText(patch.altText());
        if (patch.position() != null) img.setPosition(patch.position());
        if (patch.tagIds() != null) img.setTagIds(patch.tagIds());
        return toDto(gallery.save(img));
    }

    public void delete(AuthPrincipal actor, String imageId) {
        GalleryImage img = gallery.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such image"));
        requireManageable(actor, img.getSiteId());

        if (img.getImageKey() != null) imageStore.delete(img.getImageKey());
        if (img.getThumbKey() != null) imageStore.delete(img.getThumbKey());
        gallery.delete(img);
    }

    // ---- Guards ----

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

    private GalleryImageDto toDto(GalleryImage e) {
        return new GalleryImageDto(
                e.getId(),
                e.getSiteId(),
                e.getImageKey() == null ? null : imageStore.urlFor(e.getImageKey()),
                e.getThumbKey() == null ? null : imageStore.urlFor(e.getThumbKey()),
                e.getLqip(),
                e.getCaption(),
                e.getAltText(),
                e.getPosition(),
                e.getTagIds());
    }
}