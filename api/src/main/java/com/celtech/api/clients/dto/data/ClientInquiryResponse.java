package com.celtech.api.clients.dto.data;

/** Response to a public submit — deliberately minimal, no echo of stored data. */
public record ClientInquiryResponse(String status, String message) {}