package com.celtech.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String allowedOrigins;

    public WebConfig(@Value("${app.cors.allowed-origins}") String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = java.util.Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        if (origins.length == 0) {
            return;
        }

        // Public client-site endpoints: gallery reads + inquiry submits.
        // These need NO cookies, so credentials are off. That relaxes the strict
        // exact-origin echo rule and avoids the credentialed-CORS 403s that hit
        // cross-origin client sites (ells, ddarty).
        registry.addMapping("/api/sites/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowCredentials(false)
                .maxAge(3600);

        // Portal endpoints: the authoring app sends the auth cookie, so credentials
        // are required here. Exact-origin echo applies, which is fine — the portal
        // is a known origin in the list.
        registry.addMapping("/api/portal/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);

        // Everything else under /api (entries, packages, the celtech-solutions
        // own /api/contact) — keep the original behavior.
        registry.addMapping("/api/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * Serves the local image folder during development. Under "prod" the CDN
     * handles this and no handler is registered.
     */
    @Configuration
    @Profile("local")
    static class LocalImageResources implements WebMvcConfigurer {

        private final String root;
        private final String publicPrefix;

        LocalImageResources(
                @Value("${app.storage.local.root}") String root,
                @Value("${app.storage.local.public-prefix}") String publicPrefix) {
            this.root = root;
            this.publicPrefix = publicPrefix;
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            String location = Path.of(root).toAbsolutePath().normalize().toUri().toString();
            registry.addResourceHandler(publicPrefix + "/**")
                    .addResourceLocations(location)
                    .setCachePeriod(0);
        }
    }
}
