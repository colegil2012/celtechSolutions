package com.celtech.api.clients.dto.image;

import java.util.List;

public record GalleryImageUpdate(
        String caption,
        String altText,
        Integer position,
        List<String> tagIds) {}
