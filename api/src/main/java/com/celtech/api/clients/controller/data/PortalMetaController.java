package com.celtech.api.clients.controller.data;

import com.celtech.api.clients.auth.AuthPrincipal;
import com.celtech.api.clients.dto.data.ClientMetaDto;
import com.celtech.api.clients.dto.data.ClientMetaUpdate;
import com.celtech.api.clients.service.data.ClientMetaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/portal/sites")
public class PortalMetaController {

    private final ClientMetaService meta;

    public PortalMetaController(ClientMetaService meta) {
        this.meta = meta;
    }

    @GetMapping("/{siteId}/meta")
    public ClientMetaDto get(@AuthenticationPrincipal AuthPrincipal actor,
                             @PathVariable String siteId) {
        return meta.getForSite(actor, siteId);
    }

    @PutMapping("/{siteId}/meta")
    public ClientMetaDto update(@AuthenticationPrincipal AuthPrincipal actor,
                                @PathVariable String siteId,
                                @RequestBody ClientMetaUpdate patch) {
        return meta.update(actor, siteId, patch);
    }

    @PostMapping("/{siteId}/meta/image")
    public ClientMetaDto uploadImage(@AuthenticationPrincipal AuthPrincipal actor,
                                     @PathVariable String siteId,
                                     @RequestParam("file") MultipartFile file) {
        return meta.uploadAboutImage(actor, siteId, file);
    }
}