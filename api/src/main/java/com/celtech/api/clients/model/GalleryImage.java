package com.celtech.api.clients.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "gallery_image")
public class GalleryImage {

    @Id
    private String id;

    @Indexed
    private String siteId;

    private List<String> tagIds = new ArrayList<>();

    private String imageKey;
    private String thumbKey;
    private String lqip;

    private String caption;
    private String altText;

    private int position;

    private String uploadedBy;
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSiteId() { return siteId; }
    public void setSiteId(String siteId) { this.siteId = siteId; }

    public List<String> getTagIds() { return tagIds; }

    public void setTagIds(List<String> tagIds) { this.tagIds = tagIds; }

    public String getImageKey() { return imageKey; }
    public void setImageKey(String imageKey) { this.imageKey = imageKey; }

    public String getThumbKey() { return thumbKey; }
    public void setThumbKey(String thumbKey) { this.thumbKey = thumbKey; }

    public String getLqip() { return lqip; }
    public void setLqip(String lqip) { this.lqip = lqip; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

}