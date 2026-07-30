package com.celtech.api.dto;

import com.celtech.api.model.ServicePackage;
import java.util.List;

public record PackageDto(
        String id,
        String slug,
        String name,
        String tagline,
        int order,
        String priceType,
        Integer priceFrom,
        String priceNote,
        String summary,
        List<String> includes,
        List<ServicePackage.AddOn> addOns,
        String timeline,
        String bestFor,
        String imageUrl,
        String thumbUrl,
        String lqip,
        boolean featured,
        String ctaLabel
) {}
