package com.celtech.api.clients.controller.image;

import com.celtech.api.clients.dto.image.GalleryResponse;
import com.celtech.api.clients.service.image.GalleryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * Authless read endpoint each client's live site consumes. Cacheable so a
 * portal blip never darkens a live gallery; TTL is configurable via
 * app.public-read.cache-seconds (default 30 min).
 */
@RestController
@RequestMapping("/api/sites")
public class PublicGalleryController {

    private final GalleryService gallery;
    private final long cacheSeconds;

    public PublicGalleryController(GalleryService gallery,
                                   @Value("${app.public-read.cache-seconds:1800}") long cacheSeconds) {
        this.gallery = gallery;
        this.cacheSeconds = cacheSeconds;
    }

    @GetMapping("/{storageSlug}/gallery")
    public ResponseEntity<GalleryResponse> gallery(@PathVariable String storageSlug) {
        GalleryResponse body = gallery.listBySlug(storageSlug);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofSeconds(cacheSeconds)).cachePublic())
                .body(body);
    }
}