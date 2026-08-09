package com.celtech.api.clients.dto;

import java.util.List;

public record GalleryImageUpdate(
        String caption,
        String altText,
        Integer position,
        List<String> tagIds) {}
