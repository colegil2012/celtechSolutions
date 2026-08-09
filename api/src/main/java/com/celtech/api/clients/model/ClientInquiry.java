package com.celtech.api.clients.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * A lead captured from a client site's contact form. Multi-tenant: scoped by
 * siteId, lives in celtech-clients. Distinct from the celtech-solutions
 * `inquiry` collection (this app's own contact form) to avoid any confusion.
 */
@Document(collection = "client_inquiry")
public class ClientInquiry {

    public enum Status { NEW, READ, REPLIED, ARCHIVED }

    @Id
    private String id;

    @Indexed
    private String siteId;

    private String name;
    private String email;
    private String phone;
    private String company;
    private String message;

    /** Optional free-form subject/interest the client's form may send. */
    private String subject;

    @Indexed
    private Status status = Status.NEW;

    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSiteId() { return siteId; }
    public void setSiteId(String siteId) { this.siteId = siteId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}