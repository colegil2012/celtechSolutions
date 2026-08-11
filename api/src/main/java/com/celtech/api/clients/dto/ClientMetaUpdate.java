package com.celtech.api.clients.dto;

import com.celtech.api.clients.model.ClientMeta;

import java.util.List;

/** Editable about-page content. All optional — only provided fields change.
 *  serviceHeader, when provided, REPLACES the whole list (simplest semantics
 *  for a small, client-managed set capped at ~4). */
public record ClientMetaUpdate(
        String aboutHeader,
        ClientMeta.Section topSection,
        ClientMeta.Section midSection,
        ClientMeta.Section bottomSection,
        List<ClientMeta.Section> serviceHeader,
        String aboutImageCaption,
        String aboutImageAltText) {}