package com.celtech.api.clients.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "client_user")
public class ClientUser {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    /** BCrypt hash — never the raw password. */
    private String passwordHash;

    private String displayName;

    /** Site ids this user may manage. */
    private List<String> siteIds;

    /** "CLIENT" | "ADMIN". ADMIN (you) can manage every site. */
    private String role = "CLIENT";

    private boolean enabled = true;
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public List<String> getSiteIds() { return siteIds; }
    public void setSiteIds(List<String> siteIds) { this.siteIds = siteIds; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}