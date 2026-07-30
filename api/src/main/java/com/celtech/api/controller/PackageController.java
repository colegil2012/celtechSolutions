package com.celtech.api.controller;

import com.celtech.api.dto.PackageDto;
import com.celtech.api.service.PackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    private final PackageService service;

    public PackageController(PackageService service) {
        this.service = service;
    }

    /** GET /api/packages  or  /api/packages?featured=true */
    @GetMapping
    public List<PackageDto> list(@RequestParam(defaultValue = "false") boolean featured) {
        return featured ? service.findFeatured() : service.findAll();
    }

    /** GET /api/packages/{slug} */
    @GetMapping("/{slug}")
    public ResponseEntity<PackageDto> bySlug(@PathVariable String slug) {
        return service.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
