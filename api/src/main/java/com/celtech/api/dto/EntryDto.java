package com.celtech.api.dto;

import java.util.List;

public record EntryDto(
        String id,
        String kind,
        String name,
        String url,
        String blurb,
        String summary,
        List<String> category,
        List<String> stack,
        boolean builtByUs,
        String imageUrl,
        String thumbUrl,
        String lqip,
        boolean featured,
        String launchedYear
) {}
