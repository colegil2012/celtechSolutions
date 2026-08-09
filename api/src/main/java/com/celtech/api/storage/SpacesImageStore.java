package com.celtech.api.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * DigitalOcean Spaces implementation. Spaces speaks the S3 API, so this is the
 * standard AWS SDK v2 client pointed at a Spaces endpoint (see StorageConfig).
 * Active under the "prod" profile.
 */
@Component
@Profile("prod")
public class SpacesImageStore implements ImageStore {

    private final S3Client client;
    private final String bucket;
    private final String cdnBase;

    public SpacesImageStore(
            S3Client client,
            @Value("${app.storage.spaces.bucket}") String bucket,
            @Value("${app.storage.spaces.cdn-base}") String cdnBase) {
        this.client = client;
        this.bucket = bucket;
        this.cdnBase = cdnBase.endsWith("/")
                ? cdnBase.substring(0, cdnBase.length() - 1)
                : cdnBase;
    }

    @Override
    public String put(String key, InputStream data, String contentType) {
        try {
            byte[] bytes = data.readAllBytes();
            client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(contentType)
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .cacheControl("public, max-age=31536000, immutable")
                            .build(),
                    RequestBody.fromBytes(bytes));
        } catch (IOException e) {
            throw new UncheckedIOException("Upload failed for key: " + key, e);
        }
        return key;
    }

    /**
     * Server-side copy within the bucket. S3 has no rename, so a "move" (see the
     * default method on ImageStore) is copy + delete. ACLs are per-object and are
     * NOT carried by a copy, so we re-assert PUBLIC_READ on the destination; the
     * COPY metadata directive preserves the source's content-type/cache-control.
     */
    @Override
    public String copy(String sourceKey, String destinationKey) {
        client.copyObject(CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(sourceKey)
                .destinationBucket(bucket)
                .destinationKey(destinationKey)
                .metadataDirective(MetadataDirective.COPY)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build());
        return destinationKey;
    }

    @Override
    public String urlFor(String key) {
        return cdnBase + "/" + key;
    }

    @Override
    public void delete(String key) {
        client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }
}
