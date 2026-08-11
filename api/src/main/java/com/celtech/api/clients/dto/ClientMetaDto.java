package com.celtech.api.clients.dto;

import com.celtech.api.clients.model.ClientMeta;

import java.util.List;

/** Public + portal view of a site's about-page content. The image key is
 *  resolved to a URL; raw storage keys are never exposed. */
public record ClientMetaDto(
        String siteId,
        String aboutHeader,
        ClientMeta.Section topSection,
        ClientMeta.Section midSection,
        ClientMeta.Section bottomSection,
        List<ClientMeta.Section> serviceHeader,
        String aboutImageUrl,
        String aboutImageCaption,
        String aboutImageAltText) {}