package com.celtech.api.clients.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * A client-managed gallery tag, scoped to one site.
 *   kind="category" — classifies (Hardscaping, Portraits). One image, many.
 *   kind="album"    — groups a set of images shown together (a specific job).
 * One collection, discriminated by kind — same pattern as the public Entry model.
 */
@Document(collection = "gallery_tag")
@CompoundIndex(name = "site_slug_unique", def = "{'siteId': 1, 'slug': 1}", unique = true)
public class GalleryTag {

    @Id
    private String id;

    @Indexed
    private String siteId;

    private String label;
    private String slug;

    /** "category" | "album" */
    private String kind = "category";

    /** Optional cover image (a gallery_image._id) — used by album-kind tags. */
    private String coverImageId;

    private int position;
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSiteId() { return siteId; }
    public void setSiteId(String siteId) { this.siteId = siteId; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getCoverImageId() { return coverImageId; }
    public void setCoverImageId(String coverImageId) { this.coverImageId = coverImageId; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}