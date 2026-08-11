package com.celtech.api.clients.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@Document(collection = "client_meta")
public class ClientMeta {
    @Id
    private String id;

    @Indexed
    private String siteId;

    /* About Section Data */
    private String aboutHeader;

    private Section topSection;
    private Section midSection;
    private Section bottomSection;

    /* Optional list of small header/blurb pairs, e.g. "Founded": "2025". */
    private List<Section> serviceHeader;

    /* Storage KEY (not URL) — resolved to a URL in the DTO. */
    private String aboutImageKey;
    private String aboutImageCaption;
    private String aboutImageAltText;

    @Data
    @Builder
    public static class Section {
        private String header;
        private String section;
    }
}