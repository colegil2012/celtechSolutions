package com.celtech.api.clients;

import com.celtech.api.clients.model.user.ClientUser;
import com.celtech.api.clients.model.admin.Site;
import com.celtech.api.clients.repository.user.ClientUserRepository;
import com.celtech.api.clients.repository.admin.SiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Bootstraps the first Site + ADMIN. Non-destructive: if ANY admin exists, does
 * nothing. Runs automatically under the local profile; in prod it runs only when
 * BOOTSTRAP_ADMIN=true, so the first prod admin is created deliberately and the
 * flag can then be removed.
 */
@Component
public class PortalSeed implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PortalSeed.class);

    private static final String DEFAULT_EMAIL = "druid@celtechsolutions.tech";
    private static final String DEFAULT_NAME  = "Celtech Admin";
    private static final String DEFAULT_PASSWORD = "changeme123";
    private static final String SITE_NAME = "Celtech Solutions";
    private static final String SITE_SLUG = "celtech";

    private final ClientUserRepository users;
    private final SiteRepository sites;
    private final PasswordEncoder encoder;
    private final Environment env;

    public PortalSeed(ClientUserRepository users, SiteRepository sites,
                      PasswordEncoder encoder, Environment env) {
        this.users = users;
        this.sites = sites;
        this.encoder = encoder;
        this.env = env;
    }

    @Override
    public void run(String... args) {
        boolean isLocal = Arrays.asList(env.getActiveProfiles()).contains("local");
        boolean bootstrapEnabled = Boolean.parseBoolean(
                System.getenv().getOrDefault("BOOTSTRAP_ADMIN", "false"));

        if (!isLocal && !bootstrapEnabled) {
            return; // prod, flag off: never touch anything
        }

        boolean adminExists = users.findAll().stream()
                .anyMatch(u -> "ADMIN".equals(u.getRole()));
        if (adminExists) {
            log.info("[seed] An admin already exists — skipping bootstrap.");
            return;
        }
        Site site = ensureSite();
        bootstrapAdmin(site);
    }

    private Site ensureSite() {
        return sites.findByStorageSlug(SITE_SLUG).orElseGet(() -> {
            Site s = new Site();
            s.setName(SITE_NAME);
            s.setStorageSlug(SITE_SLUG);
            s.setEnabled(true);
            Site saved = sites.save(s);
            log.info("[seed] Created site '{}' (slug={}, id={})",
                    saved.getName(), saved.getStorageSlug(), saved.getId());
            return saved;
        });
    }

    private void bootstrapAdmin(Site site) {
        String email = System.getenv().getOrDefault("BOOTSTRAP_ADMIN_EMAIL", DEFAULT_EMAIL);
        String name  = System.getenv().getOrDefault("BOOTSTRAP_ADMIN_NAME", DEFAULT_NAME);
        String pass  = System.getenv().getOrDefault("BOOTSTRAP_ADMIN_PASSWORD", DEFAULT_PASSWORD);

        ClientUser u = new ClientUser();
        u.setEmail(email);
        u.setDisplayName(name);
        u.setPasswordHash(encoder.encode(pass));
        u.setRole("ADMIN");
        u.setSiteIds(List.of(site.getId()));
        u.setEnabled(true);
        users.save(u);
        log.warn("[seed] Bootstrapped first ADMIN '{}'. CHANGE THE PASSWORD, then unset BOOTSTRAP_ADMIN.", email);
    }
}