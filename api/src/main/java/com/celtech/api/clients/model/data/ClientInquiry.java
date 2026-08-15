package com.celtech.api.clients.model.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * A lead captured from a client site's contact form. Multi-tenant: scoped by
 * siteId, lives in celtech-clients. Distinct from the celtech-solutions
 * `inquiry` collection (this app's own contact form) to avoid any confusion.
 */
@Setter
@Getter
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

}