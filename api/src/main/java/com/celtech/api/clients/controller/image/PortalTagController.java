package com.celtech.api.clients.controller.image;

import com.celtech.api.clients.auth.AuthPrincipal;
import com.celtech.api.clients.dto.image.GalleryTagDto;
import com.celtech.api.clients.dto.image.GalleryTagRequest;
import com.celtech.api.clients.service.image.GalleryTagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portal/sites")
public class PortalTagController {

    private final GalleryTagService tags;

    public PortalTagController(GalleryTagService tags) {
        this.tags = tags;
    }

    @GetMapping("/{siteId}/tags")
    public List<GalleryTagDto> list(@PathVariable String siteId) {
        // Listing is read-only vocabulary; siteId scopes it. (Ownership is
        // enforced on all writes below.)
        return tags.listForSite(siteId);
    }

    @PostMapping("/{siteId}/tags")
    public GalleryTagDto create(@AuthenticationPrincipal AuthPrincipal actor,
                                @PathVariable String siteId,
                                @Valid @RequestBody GalleryTagRequest req) {
        return tags.create(actor, siteId, req);
    }

    @PutMapping("/tags/{tagId}")
    public GalleryTagDto update(@AuthenticationPrincipal AuthPrincipal actor,
                                @PathVariable String tagId,
                                @RequestBody GalleryTagRequest req) {
        return tags.update(actor, tagId, req);
    }

    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthPrincipal actor,
                                       @PathVariable String tagId) {
        tags.delete(actor, tagId);
        return ResponseEntity.noContent().build();
    }
}