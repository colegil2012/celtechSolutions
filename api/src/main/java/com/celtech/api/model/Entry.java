package com.celtech.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * A linked thing: either work we built (kind="portfolio") or a local business
 * we recommend (kind="directory"). One collection, discriminated by kind, so
 * the site can grow into a full local directory by adding fields rather than
 * restructuring.
 */
@Document(collection = "entry")
public class Entry {

    @Id
    private String id;

    /** "portfolio" | "directory" */
    @Indexed
    private String kind;

    private String name;
    private String url;
    private String blurb;
    private String summary;

    @Indexed
    private List<String> category;

    /** Technologies — portfolio only; empty for directory entries. */
    private List<String> stack;

    private boolean builtByUs;

    private String imageKey;
    private String thumbKey;
    private String lqip;

    @Indexed
    private boolean featured;

    private String launchedYear;
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getBlurb() { return blurb; }
    public void setBlurb(String blurb) { this.blurb = blurb; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getCategory() { return category; }
    public void setCategory(List<String> category) { this.category = category; }

    public List<String> getStack() { return stack; }
    public void setStack(List<String> stack) { this.stack = stack; }

    public boolean isBuiltByUs() { return builtByUs; }
    public void setBuiltByUs(boolean builtByUs) { this.builtByUs = builtByUs; }

    public String getImageKey() { return imageKey; }
    public void setImageKey(String imageKey) { this.imageKey = imageKey; }

    public String getThumbKey() { return thumbKey; }
    public void setThumbKey(String thumbKey) { this.thumbKey = thumbKey; }

    public String getLqip() { return lqip; }
    public void setLqip(String lqip) { this.lqip = lqip; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public String getLaunchedYear() { return launchedYear; }
    public void setLaunchedYear(String launchedYear) { this.launchedYear = launchedYear; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
