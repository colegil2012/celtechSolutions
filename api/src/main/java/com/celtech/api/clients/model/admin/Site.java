package com.celtech.api.clients.model.admin;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "site")
public class Site {

    @Id
    private String id;

    /** Human name, e.g. "Ell's Landscaping". */
    private String name;

    /**
     * Storage + URL slug, e.g. "ddarty-art". Drives Spaces keys
     * ({storageSlug}/full, {storageSlug}/thumb) and public read URLs.
     */
    @Indexed(unique = true)
    private String storageSlug;

    /**
     * Email address to notify when a new inquiry is received. Set per client.
     */
    private String notifyEmail;

    private SiteConfig config;

    private boolean enabled = true;
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStorageSlug() { return storageSlug; }
    public void setStorageSlug(String storageSlug) { this.storageSlug = storageSlug; }

    public String getNotifyEmail() { return notifyEmail; }
    public void setNotifyEmail(String notifyEmail) { this.notifyEmail = notifyEmail; }

    public SiteConfig getConfig() { return config == null ? SiteConfig.defaults() : config; }
    public void setConfig(SiteConfig config) { this.config = config; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}