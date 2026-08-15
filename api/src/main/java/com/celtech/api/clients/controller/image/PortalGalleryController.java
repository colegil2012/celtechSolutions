package com.celtech.api.clients.controller.image;

import com.celtech.api.clients.auth.AuthPrincipal;
import com.celtech.api.clients.dto.image.GalleryImageDto;
import com.celtech.api.clients.dto.image.GalleryImageUpdate;
import com.celtech.api.clients.service.image.GalleryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/portal/sites")
public class PortalGalleryController {

    private final GalleryService gallery;

    public PortalGalleryController(GalleryService gallery) {
        this.gallery = gallery;
    }

    @PostMapping("/{siteId}/gallery")
    public GalleryImageDto upload(@AuthenticationPrincipal AuthPrincipal actor,
                                  @PathVariable String siteId,
                                  @RequestParam("file") MultipartFile file) {
        return gallery.upload(actor, siteId, file);
    }

    @PutMapping("/gallery/{imageId}")
    public GalleryImageDto update(@AuthenticationPrincipal AuthPrincipal actor,
                                  @PathVariable String imageId,
                                  @RequestBody GalleryImageUpdate patch) {
        return gallery.update(actor, imageId, patch);
    }

    @DeleteMapping("/gallery/{imageId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthPrincipal actor,
                                       @PathVariable String imageId) {
        gallery.delete(actor, imageId);
        return ResponseEntity.noContent().build();
    }
}