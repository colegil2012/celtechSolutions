package com.celtech.api.clients.service;

import com.celtech.api.clients.auth.AuthPrincipal;
import com.celtech.api.clients.dto.GalleryTagDto;
import com.celtech.api.clients.dto.GalleryTagRequest;
import com.celtech.api.clients.model.GalleryImage;
import com.celtech.api.clients.model.GalleryTag;
import com.celtech.api.clients.repository.GalleryImageRepository;
import com.celtech.api.clients.repository.GalleryTagRepository;
import com.celtech.api.clients.repository.SiteRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class GalleryTagService {

    private final GalleryTagRepository tags;
    private final GalleryImageRepository images;
    private final SiteRepository sites;

    public GalleryTagService(GalleryTagRepository tags,
                             GalleryImageRepository images,
                             SiteRepository sites) {
        this.tags = tags;
        this.images = images;
        this.sites = sites;
    }

    public List<GalleryTagDto> listForSite(String siteId) {
        return tags.findBySiteIdOrderByPositionAsc(siteId).stream().map(this::toDto).toList();
    }

    public GalleryTagDto create(AuthPrincipal actor, String siteId, GalleryTagRequest req) {
        requireManageable(actor, siteId);
        GalleryTag t = new GalleryTag();
        t.setSiteId(siteId);
        t.setLabel(req.label().trim());
        t.setSlug(slugify(req.slug() != null && !req.slug().isBlank() ? req.slug() : req.label()));
        t.setKind(normalizeKind(req.kind()));
        t.setCoverImageId(req.coverImageId());
        t.setPosition(req.position() != null ? req.position()
                : tags.findBySiteIdOrderByPositionAsc(siteId).size());
        return saveUnique(t);
    }

    public GalleryTagDto update(AuthPrincipal actor, String tagId, GalleryTagRequest req) {
        GalleryTag t = tags.findById(tagId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such tag"));
        requireManageable(actor, t.getSiteId());

        if (req.label() != null && !req.label().isBlank()) t.setLabel(req.label().trim());
        if (req.slug() != null && !req.slug().isBlank())   t.setSlug(slugify(req.slug()));
        if (req.kind() != null)                            t.setKind(normalizeKind(req.kind()));
        if (req.coverImageId() != null)                    t.setCoverImageId(req.coverImageId());
        if (req.position() != null)                        t.setPosition(req.position());
        return saveUnique(t);
    }

    /** Deleting a tag also strips its id from every image that referenced it. */
    public void delete(AuthPrincipal actor, String tagId) {
        GalleryTag t = tags.findById(tagId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such tag"));
        requireManageable(actor, t.getSiteId());

        List<GalleryImage> affected = images.findBySiteIdOrderByPositionAsc(t.getSiteId());
        for (GalleryImage img : affected) {
            if (img.getTagIds() != null && img.getTagIds().remove(tagId)) {
                images.save(img);
            }
        }
        tags.delete(t);
    }

    // ---- helpers ----

    private GalleryTagDto saveUnique(GalleryTag t) {
        try {
            return toDto(tags.save(t));
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A tag with that slug already exists for this site");
        }
    }

    private void requireManageable(AuthPrincipal actor, String siteId) {
        if (!actor.canManage(siteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your site");
        }
        sites.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown site"));
    }

    private String normalizeKind(String kind) {
        if (kind == null) return "category";
        return switch (kind.toLowerCase(Locale.ROOT)) {
            case "album" -> "album";
            default -> "category";
        };
    }

    private String slugify(String s) {
        String slug = s.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (slug.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag needs a usable label/slug");
        }
        return slug;
    }

    private GalleryTagDto toDto(GalleryTag t) {
        return new GalleryTagDto(t.getId(), t.getSiteId(), t.getLabel(), t.getSlug(),
                t.getKind(), t.getCoverImageId(), t.getPosition());
    }
}