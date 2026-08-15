package com.celtech.api.clients.dto.data;

import com.celtech.api.clients.model.admin.Section;

import java.util.List;

/** Public + portal view of a site's about-page content. The image key is
 *  resolved to a URL; raw storage keys are never exposed. */
public record ClientMetaDto(
        String siteId,
        String aboutHeader,
        List<Section> bioSections,
        List<Section> serviceHeader,
        String aboutImageUrl,
        String aboutImageCaption,
        String aboutImageAltText) {}