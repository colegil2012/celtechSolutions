package com.celtech.api.clients.model.data;

import com.celtech.api.clients.model.admin.Section;
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

    /* Main Bio Sections */
    private List<Section> bioSections;

    /* Optional list of small header/blurb pairs, e.g. "Founded": "2025". */
    private List<Section> serviceHeader;

    /* Storage KEY (not URL) — resolved to a URL in the DTO. */
    private String aboutImageKey;
    private String aboutImageCaption;
    private String aboutImageAltText;

}