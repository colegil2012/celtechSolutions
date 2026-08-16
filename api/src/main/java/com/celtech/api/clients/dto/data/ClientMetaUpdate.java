package com.celtech.api.clients.dto.data;

import com.celtech.api.clients.model.admin.Section;

import java.util.List;

/** Editable about-page content. All optional — only provided fields change.
 *  serviceHeader, when provided, REPLACES the whole list (simplest semantics
 *  for a small, client-managed set capped at ~4). */
public record ClientMetaUpdate(
        String aboutHeader,
        List<Section> bioSections,
        List<Section> serviceHeader,
        String aboutImageKey,
        String aboutImageCaption,
        String aboutImageAltText) {}