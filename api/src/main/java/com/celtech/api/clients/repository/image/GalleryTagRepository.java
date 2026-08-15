package com.celtech.api.clients.repository.image;

import com.celtech.api.clients.model.image.GalleryTag;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GalleryTagRepository extends MongoRepository<GalleryTag, String> {
    List<GalleryTag> findBySiteIdOrderByPositionAsc(String siteId);
    Optional<GalleryTag> findBySiteIdAndSlug(String siteId, String slug);
}