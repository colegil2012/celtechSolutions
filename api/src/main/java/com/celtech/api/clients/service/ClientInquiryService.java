package com.celtech.api.clients.service;

import com.celtech.api.clients.auth.AuthPrincipal;
import com.celtech.api.clients.dto.ClientInquiryDto;
import com.celtech.api.clients.dto.ClientInquiryRequest;
import com.celtech.api.clients.dto.InquiryStatusUpdate;
import com.celtech.api.clients.model.ClientInquiry;
import com.celtech.api.clients.model.Site;
import com.celtech.api.clients.repository.ClientInquiryRepository;
import com.celtech.api.clients.repository.SiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class ClientInquiryService {

    private static final Logger log = LoggerFactory.getLogger(ClientInquiryService.class);

    private final ClientInquiryRepository inquiries;
    private final SiteRepository sites;
    private final JavaMailSender mailSender;
    private final String notifyFrom;
    private final String fallbackNotifyTo;
    private final boolean mailEnabled;

    public ClientInquiryService(ClientInquiryRepository inquiries,
                                SiteRepository sites,
                                JavaMailSender mailSender,
                                @Value("${app.mail.notify-from}") String notifyFrom,
                                @Value("${app.mail.notify-to}") String fallbackNotifyTo,
                                @Value("${app.mail.enabled:false}") boolean mailEnabled) {
        this.inquiries = inquiries;
        this.sites = sites;
        this.mailSender = mailSender;
        this.notifyFrom = notifyFrom;
        this.fallbackNotifyTo = fallbackNotifyTo;
        this.mailEnabled = mailEnabled;
    }

    // ---- Public submit (authless) ----

    /**
     * A lost lead is unrecoverable; a missing DB row can be reconciled from the
     * email. So we attempt the save, then ALWAYS notify — if the save failed we
     * log loudly and still email, so no lead is silently dropped.
     */
    public void submit(String storageSlug, ClientInquiryRequest req) {
        Site site = sites.findByStorageSlug(storageSlug)
                .filter(Site::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown site"));

        // Honeypot: accept silently (bot sees success), store nothing.
        if (req.website() != null && !req.website().isBlank()) {
            return;
        }

        ClientInquiry saved = null;
        try {
            ClientInquiry ci = new ClientInquiry();
            ci.setSiteId(site.getId());
            ci.setName(req.name().trim());
            ci.setEmail(req.email().trim().toLowerCase(Locale.ROOT));
            ci.setPhone(req.phone());
            ci.setCompany(req.company());
            ci.setMessage(req.message().trim());
            ci.setSubject(req.subject());
            saved = inquiries.save(ci);
        } catch (Exception e) {
            // DB write failed — do NOT abort. Email is the backstop below.
            log.error("[inquiry] DB save failed for site {} — sending email backstop. Payload from {}",
                    site.getStorageSlug(), req.email(), e);
        }

        notify(site, req, saved);
    }

    private void notify(Site site, ClientInquiryRequest req, ClientInquiry saved) {
        if (!mailEnabled) {
            log.info("[inquiry] Mail disabled; site={} lead from {} stored={}",
                    site.getStorageSlug(), req.email(), saved != null);
            return;
        }
        String to = (site.getNotifyEmail() != null && !site.getNotifyEmail().isBlank())
                ? site.getNotifyEmail() : fallbackNotifyTo;
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setFrom(notifyFrom);
            mail.setReplyTo(req.email());
            mail.setSubject("New lead — " + site.getName() + " — " + req.name());
            mail.setText("""
                    Site: %s
                    Name: %s
                    Company: %s
                    Email: %s
                    Phone: %s
                    Subject: %s
                    %s

                    %s
                    """.formatted(
                    site.getName(),
                    req.name(),
                    orDash(req.company()),
                    req.email(),
                    orDash(req.phone()),
                    orDash(req.subject()),
                    saved == null ? "\n[WARNING] DB save failed — this lead is NOT stored. Reconcile manually." : "",
                    req.message()));
            mailSender.send(mail);
        } catch (MailException e) {
            // Both DB and email failed — this is the worst case; log at error with the full payload.
            log.error("[inquiry] EMAIL FAILED for site {} lead from {} name={} phone={} msg={}",
                    site.getStorageSlug(), req.email(), req.name(), req.phone(), req.message(), e);
        }
    }

    // ---- Portal read/manage (authorized, ownership-checked) ----

    public List<ClientInquiryDto> listForSite(AuthPrincipal actor, String siteId) {
        requireManageable(actor, siteId);
        return inquiries.findBySiteIdOrderByCreatedAtDesc(siteId).stream().map(this::toDto).toList();
    }

    public ClientInquiryDto updateStatus(AuthPrincipal actor, String inquiryId, String status) {
        ClientInquiry ci = inquiries.findById(inquiryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such inquiry"));
        requireManageable(actor, ci.getSiteId());
        try {
            ci.setStatus(ClientInquiry.Status.valueOf(status.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }
        return toDto(inquiries.save(ci));
    }

    public void delete(AuthPrincipal actor, String inquiryId) {
        ClientInquiry ci = inquiries.findById(inquiryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such inquiry"));
        requireManageable(actor, ci.getSiteId());
        inquiries.delete(ci);
    }

    // ---- Guards / helpers ----

    private void requireManageable(AuthPrincipal actor, String siteId) {
        if (!actor.canManage(siteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your site");
        }
        sites.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown site"));
    }

    private String orDash(String v) { return (v == null || v.isBlank()) ? "—" : v; }

    private ClientInquiryDto toDto(ClientInquiry c) {
        return new ClientInquiryDto(c.getId(), c.getSiteId(), c.getName(), c.getEmail(),
                c.getPhone(), c.getCompany(), c.getMessage(), c.getSubject(),
                c.getStatus().name(), c.getCreatedAt());
    }
}