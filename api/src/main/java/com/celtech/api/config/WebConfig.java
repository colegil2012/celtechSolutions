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
        // Split, then drop blanks. When api and web share a hostname (the
        // production setup) there are no cross-origin requests, so this list
        // is empty and no CORS rule is registered at all. Passing a lone ""
        // origin would otherwise register an invalid rule.
        String[] origins = java.util.Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        if (origins.length == 0) {
            return;
        }

        registry.addMapping("/api/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "OPTIONS")
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
