package com.celtech.api.clients.controller;

import com.celtech.api.clients.dto.ClientInquiryRequest;
import com.celtech.api.clients.dto.ClientInquiryResponse;
import com.celtech.api.clients.service.ClientInquiryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authless lead-capture endpoint each client site's contact form POSTs to.
 * Off the /api/portal/** prefix so Security never touches it. CORS for client
 * origins is configured in WebConfig / app.cors.allowed-origins.
 */
@RestController
@RequestMapping("/api/sites")
public class PublicInquiryController {

    private final ClientInquiryService service;

    public PublicInquiryController(ClientInquiryService service) {
        this.service = service;
    }

    @PostMapping("/{storageSlug}/inquiries")
    public ResponseEntity<ClientInquiryResponse> submit(@PathVariable String storageSlug,
                                                        @Valid @RequestBody ClientInquiryRequest req) {
        service.submit(storageSlug, req);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ClientInquiryResponse("received",
                        "Thanks — your message has been sent."));
    }
}