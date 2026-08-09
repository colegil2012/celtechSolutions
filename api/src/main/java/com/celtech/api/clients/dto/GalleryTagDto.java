package com.celtech.api.clients.dto;

public record GalleryTagDto(
        String id,
        String siteId,
        String label,
        String slug,
        String kind,
        String coverImageId,
        int position) {}