package com.celtech.api.clients.repository.image;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.celtech.api.clients.model.image.GalleryImage;

import java.util.List;

public interface GalleryImageRepository extends MongoRepository<GalleryImage, String> {
    List<GalleryImage> findBySiteIdOrderByPositionAsc(String siteId);
}
