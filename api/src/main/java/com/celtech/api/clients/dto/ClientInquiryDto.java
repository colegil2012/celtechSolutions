package com.celtech.api.clients.dto;

import java.time.Instant;

/** Portal-facing view of a stored inquiry. */
public record ClientInquiryDto(
        String id,
        String siteId,
        String name,
        String email,
        String phone,
        String company,
        String message,
        String subject,
        String status,
        Instant createdAt) {}