package com.celtech.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * A sellable package. Fully DB-driven: the packages page and each detail page
 * render entirely from these documents, so marketing edits are a re-seed, not
 * a code change.
 */
@Document(collection = "service_package")
public class ServicePackage {

    /** An optional upsell attached to a package. */
    public static class AddOn {
        private String name;
        private String note;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }

    @Id
    private String id;

    /** URL segment and stable identifier: /packages/{slug} */
    @Indexed(unique = true)
    private String slug;

    private String name;
    private String tagline;
    private int order;

    /** "fixed" | "from" | "quote" */
    private String priceType;
    /** Whole dollars, or null for quote-type. */
    private Integer priceFrom;
    private String priceNote;

    private String summary;
    private List<String> includes;
    private List<AddOn> addOns;
    private String timeline;
    private String bestFor;

    private String imageKey;
    private String thumbKey;
    private String lqip;

    @Indexed
    private boolean featured;
    private String ctaLabel;

    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public String getPriceType() { return priceType; }
    public void setPriceType(String priceType) { this.priceType = priceType; }

    public Integer getPriceFrom() { return priceFrom; }
    public void setPriceFrom(Integer priceFrom) { this.priceFrom = priceFrom; }

    public String getPriceNote() { return priceNote; }
    public void setPriceNote(String priceNote) { this.priceNote = priceNote; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getIncludes() { return includes; }
    public void setIncludes(List<String> includes) { this.includes = includes; }

    public List<AddOn> getAddOns() { return addOns; }
    public void setAddOns(List<AddOn> addOns) { this.addOns = addOns; }

    public String getTimeline() { return timeline; }
    public void setTimeline(String timeline) { this.timeline = timeline; }

    public String getBestFor() { return bestFor; }
    public void setBestFor(String bestFor) { this.bestFor = bestFor; }

    public String getImageKey() { return imageKey; }
    public void setImageKey(String imageKey) { this.imageKey = imageKey; }

    public String getThumbKey() { return thumbKey; }
    public void setThumbKey(String thumbKey) { this.thumbKey = thumbKey; }

    public String getLqip() { return lqip; }
    public void setLqip(String lqip) { this.lqip = lqip; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public String getCtaLabel() { return ctaLabel; }
    public void setCtaLabel(String ctaLabel) { this.ctaLabel = ctaLabel; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
