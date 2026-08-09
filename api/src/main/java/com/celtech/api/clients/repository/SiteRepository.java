package com.celtech.api.clients.repository;

import com.celtech.api.clients.model.Site;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SiteRepository extends MongoRepository<Site, String> {
    Optional<Site> findByStorageSlug(String storageSlug);
}
