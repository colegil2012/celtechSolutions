package com.ua.estore.celtechSolutions.controllers;

import com.ua.estore.celtechSolutions.models.dto.ContactForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ContactController {
    @PostMapping("/contact")
    public String submit(@Valid @ModelAttribute("contactForm") ContactForm form,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contact";
        }

        // TODO: wire up real email delivery to solutions@celtechgs.com
        // (e.g. spring-boot-starter-mail + SMTP, or an API like Postmark/SES).
        log.info("New contact submission from {} <{}>: subject='{}'",
                form.getName(), form.getEmail(), form.getSubject());

        redirectAttributes.addFlashAttribute("submitted", true);
        return "redirect:/contact";
    }
}
