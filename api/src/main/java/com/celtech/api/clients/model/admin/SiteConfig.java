package com.celtech.api.clients.model.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Per-site layout/feature config, editable from the admin portal. Embedded on
 * {@link Site} (1:1, always loaded with the site) — editing it $sets onto the
 * same site document, so a blank site simply gains this block on first save.
 *
 * Its job: make the client's portal editor mirror the fields their live app
 * actually renders. When you deploy a client app that uses different fields,
 * update this here to match.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteConfig {

    /** Number of freeform bio sections shown on the About editor. */
    @Builder.Default
    private int bioSectionCount = 3;

    /** Whether the About editor shows the service-header label/value pairs. */
    @Builder.Default
    private boolean serviceHeadersEnabled = false;

    /** Max service-header rows when enabled. */
    @Builder.Default
    private int serviceHeaderCount = 0;

    /** Whether the About editor shows the main image + caption/alt. */
    @Builder.Default
    private boolean aboutImageEnabled = true;

    /** Whether the gallery editor supports grouping images into albums. */
    @Builder.Default
    private boolean albumsEnabled = false;

    public static SiteConfig defaults() {
        return SiteConfig.builder().build();
    }
}