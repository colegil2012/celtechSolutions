package com.celtech.api.clients.dto;

import java.util.List;

public record GalleryImageDto(
        String id,
        String siteId,
        String imageUrl,
        String thumbUrl,
        String lqip,
        String caption,
        String altText,
        int position,
        List<String> tagIds) {}
