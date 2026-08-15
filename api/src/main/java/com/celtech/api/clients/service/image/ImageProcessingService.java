package com.celtech.api.clients.service.image;

import com.celtech.api.storage.ImageStore;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class ImageProcessingService {
    private static final int THUMB_MAX = 600;
    private static final int LQIP_WIDTH = 20;

    private final ImageStore imageStore;

    public ImageProcessingService(ImageStore imageStore) {
        this.imageStore = imageStore;
    }

    public record StoredImage(String imageKey, String thumbKey, String lqip) {}

    public StoredImage process(String storageSlug, MultipartFile file) {
        String ext = extensionFor(file.getContentType());
        String base = UUID.randomUUID().toString();
        String imageKey = storageSlug + "/full/" + base + "." + ext;
        String thumbKey = storageSlug + "/thumb/" + base + "." + ext;

        try {
            byte[] original = file.getBytes();

            // Full — store as uploaded.
            imageStore.put(imageKey, new ByteArrayInputStream(original), file.getContentType());

            // Thumbnail.
            byte[] thumb = resize(original, THUMB_MAX, ext);
            imageStore.put(thumbKey, new ByteArrayInputStream(thumb), file.getContentType());

            // LQIP data URI.
            String lqip = buildLqip(original, file.getContentType());

            return new StoredImage(imageKey, thumbKey, lqip);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to process upload", e);
        }
    }

    private byte[] resize(byte[] src, int maxDim, String ext) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(src))
                .size(maxDim, maxDim)
                .keepAspectRatio(true)
                .outputQuality(0.8)
                .outputFormat(ext)
                .toOutputStream(out);
        return out.toByteArray();
    }

    private String buildLqip(byte[] src, String contentType) throws IOException {
        BufferedImage tiny = Thumbnails.of(new ByteArrayInputStream(src))
                .width(LQIP_WIDTH)
                .asBufferedImage();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(tiny, "jpeg", out);
        String b64 = Base64.getEncoder().encodeToString(out.toByteArray());
        return "data:image/jpeg;base64," + b64;
    }

    private String extensionFor(String contentType) {
        if (contentType == null) return "jpg";
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/gif" -> "gif";
            default -> "jpg";
        };
    }
}
