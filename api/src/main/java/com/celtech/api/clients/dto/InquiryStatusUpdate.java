package com.celtech.api.clients.dto;

import jakarta.validation.constraints.NotBlank;

/** Portal action: mark an inquiry NEW/READ/REPLIED/ARCHIVED. */
public record InquiryStatusUpdate(@NotBlank String status) {}