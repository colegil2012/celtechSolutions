package com.celtech.api.controller;

import com.celtech.api.dto.InquiryRequest;
import com.celtech.api.dto.InquiryResponse;
import com.celtech.api.model.Inquiry;
import com.celtech.api.service.InquiryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final InquiryService service;

    public ContactController(InquiryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<InquiryResponse> submit(@Valid @RequestBody InquiryRequest request) {

        // Honeypot tripped: accept silently so the bot sees success.
        if (request.website() != null && !request.website().isBlank()) {
            return ResponseEntity.accepted()
                    .body(new InquiryResponse(null, "accepted", "Thanks — your message is in."));
        }

        Inquiry saved = service.submit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new InquiryResponse(
                        saved.getId(), "received",
                        "Thanks — we'll get back to you within one business day."));
    }
}
