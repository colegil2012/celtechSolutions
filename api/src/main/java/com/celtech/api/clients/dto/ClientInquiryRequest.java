package com.celtech.api.clients.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Public contact-form payload from a client site. */
public record ClientInquiryRequest(
        @NotBlank(message = "Enter your name") @Size(max = 120) String name,
        @NotBlank(message = "Enter your email") @Email @Size(max = 200) String email,
        @Size(max = 40) String phone,
        @Size(max = 160) String company,
        @NotBlank(message = "Enter a message") @Size(max = 4000) String message,
        @Size(max = 160) String subject,
        /** Honeypot: must stay empty. */
        String website) {}