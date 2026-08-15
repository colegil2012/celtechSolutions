package com.celtech.api.clients.controller.data;

import com.celtech.api.clients.dto.data.ClientMetaDto;
import com.celtech.api.clients.service.data.ClientMetaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * Authless read of a site's about-page content, consumed by the client's live
 * SvelteKit site. Cached like the gallery read.
 */
@RestController
@RequestMapping("/api/sites")
public class PublicMetaController {

    private final ClientMetaService meta;
    private final long cacheSeconds;

    public PublicMetaController(ClientMetaService meta,
                                @Value("${app.public-read.cache-seconds:1800}") long cacheSeconds) {
        this.meta = meta;
        this.cacheSeconds = cacheSeconds;
    }

    @GetMapping("/{storageSlug}/meta")
    public ResponseEntity<ClientMetaDto> meta(@PathVariable String storageSlug) {
        ClientMetaDto body = meta.getBySlug(storageSlug);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofSeconds(cacheSeconds)).cachePublic())
                .body(body);
    }
}