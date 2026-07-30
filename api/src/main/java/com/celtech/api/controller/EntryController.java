package com.celtech.api.controller;

import com.celtech.api.dto.EntryDto;
import com.celtech.api.service.EntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    private final EntryService service;

    public EntryController(EntryService service) {
        this.service = service;
    }

    /**
     * GET /api/entries?kind=portfolio
     * GET /api/entries?kind=directory&random=true&limit=6
     * GET /api/entries?kind=portfolio&featured=true
     * GET /api/entries?category=web
     */
    @GetMapping
    public List<EntryDto> list(
            @RequestParam(defaultValue = "portfolio") String kind,
            @RequestParam(defaultValue = "false") boolean random,
            @RequestParam(defaultValue = "false") boolean featured,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "12") int limit) {

        if (category != null && !category.isBlank()) return service.findByCategory(category);
        if (random) return service.findRandom(kind, limit);
        if (featured) return service.findFeatured(kind);
        return service.findByKind(kind);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntryDto> byId(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
