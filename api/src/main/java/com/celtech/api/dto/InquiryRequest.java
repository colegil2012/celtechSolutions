package com.celtech.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InquiryRequest(

        @NotBlank(message = "Enter your name")
        @Size(max = 120, message = "Name is too long")
        String name,

        @NotBlank(message = "Enter your email address")
        @Email(message = "Enter a valid email address")
        @Size(max = 200)
        String email,

        @Size(max = 40)
        String phone,

        @Size(max = 160)
        String company,

        @NotBlank(message = "Tell us a little about what you need")
        @Size(max = 4000, message = "Message is too long")
        String message,

        String interest,
        String budgetRange,

        /** Honeypot: must stay empty. */
        String website
) {}
