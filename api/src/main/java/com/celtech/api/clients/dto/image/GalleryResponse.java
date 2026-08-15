package com.celtech.api.clients.dto.image;

import java.util.List;

/** The public gallery payload: the site's tag vocabulary plus its images. */
public record GalleryResponse(
        List<GalleryTagDto> tags,
        List<GalleryImageDto> images) {}