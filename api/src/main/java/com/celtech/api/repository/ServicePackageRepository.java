package com.celtech.api.repository;

import com.celtech.api.model.ServicePackage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ServicePackageRepository extends MongoRepository<ServicePackage, String> {
    List<ServicePackage> findAllByOrderByOrderAsc();
    List<ServicePackage> findByFeaturedTrueOrderByOrderAsc();
    Optional<ServicePackage> findBySlug(String slug);
}
