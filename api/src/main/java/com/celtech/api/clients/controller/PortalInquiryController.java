package com.celtech.api.clients.controller;

import com.celtech.api.clients.auth.AuthPrincipal;
import com.celtech.api.clients.dto.ClientInquiryDto;
import com.celtech.api.clients.dto.InquiryStatusUpdate;
import com.celtech.api.clients.service.ClientInquiryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portal/sites")
public class PortalInquiryController {

    private final ClientInquiryService service;

    public PortalInquiryController(ClientInquiryService service) {
        this.service = service;
    }

    @GetMapping("/{siteId}/inquiries")
    public List<ClientInquiryDto> list(@AuthenticationPrincipal AuthPrincipal actor,
                                       @PathVariable String siteId) {
        return service.listForSite(actor, siteId);
    }

    @PutMapping("/inquiries/{inquiryId}/status")
    public ClientInquiryDto updateStatus(@AuthenticationPrincipal AuthPrincipal actor,
                                         @PathVariable String inquiryId,
                                         @Valid @RequestBody InquiryStatusUpdate req) {
        return service.updateStatus(actor, inquiryId, req.status());
    }

    @DeleteMapping("/inquiries/{inquiryId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthPrincipal actor,
                                       @PathVariable String inquiryId) {
        service.delete(actor, inquiryId);
        return ResponseEntity.noContent().build();
    }
}