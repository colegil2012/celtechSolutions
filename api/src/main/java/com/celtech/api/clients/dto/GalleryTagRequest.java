package com.celtech.api.clients.dto;

import jakarta.validation.constraints.NotBlank;

/** Create/update payload for a tag. slug is derived server-side from label if blank. */
public record GalleryTagRequest(
        @NotBlank String label,
        String slug,
        String kind,          // "category" | "album"; defaults to "category"
        String coverImageId,
        Integer position) {}
