package com.celtech.api.clients.dto.image;

public record GalleryTagDto(
        String id,
        String siteId,
        String label,
        String slug,
        String kind,
        String coverImageId,
        int position) {}