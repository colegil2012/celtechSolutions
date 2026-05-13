package com.ua.estore.celtechSolutions.services;

import com.ua.estore.celtechSolutions.models.dto.ContactForm;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Sends contact-form submissions to the team inbox.
 * <p>
 * The send is marked {@link Async} so the user isn't blocked waiting for the
 * SMTP round-trip. Make sure {@code @EnableAsync} is present on your main
 * application class (or any {@code @Configuration} class).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContactMailer {

    private final JavaMailSender mailSender;

    @Value("${app.mail.to:cole@celtechgs.com}")
    private String toAddress;

    @Value("${app.mail.from:solutions@celtechgs.com}")
    private String fromAddress;

    /**
     * Build and send the contact notification email.
     *
     * @param form the validated contact form data
     */
    @Async
    public void send(ContactForm form) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");

            helper.setTo(toAddress);
            helper.setFrom(fromAddress);
            // Reply-To = the visitor's address so "Reply" goes straight to them
            helper.setReplyTo(form.getEmail());
            helper.setSubject("[Contact] " + form.getSubject());
            helper.setText(buildBody(form), /* html */ false);

            mailSender.send(mime);
            log.info("Contact email sent for {} <{}>", form.getName(), form.getEmail());

        } catch (MessagingException | MailException ex) {
            log.error("Failed to send contact email for {}: {}",
                    form.getEmail(), ex.getMessage(), ex);
            // Depending on requirements you could:
            //   • re-throw a custom exception and let a global handler notify you
            //   • push the form into a retry queue / dead-letter table
        }
    }

    private String buildBody(ContactForm form) {
        StringBuilder sb = new StringBuilder();
        sb.append("New Contact form submission\n");
        sb.append("==========================\n\n");
        sb.append("Name:    ").append(form.getName()).append('\n');
        sb.append("Email:   ").append(form.getEmail()).append('\n');
        if (form.getCompany() != null && !form.getCompany().isBlank()) {
            sb.append("Company: ").append(form.getCompany()).append('\n');
        }
        sb.append("Subject: ").append(form.getSubject()).append('\n');
        sb.append("\n--- Message ---\n\n");
        sb.append(form.getMessage()).append('\n');
        return sb.toString();
    }
}