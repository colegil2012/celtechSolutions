package com.celtech.api.service;

import com.celtech.api.dto.PackageDto;
import com.celtech.api.model.ServicePackage;
import com.celtech.api.repository.ServicePackageRepository;
import com.celtech.api.storage.ImageStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PackageService {

    private final ServicePackageRepository repository;
    private final ImageStore imageStore;

    public PackageService(ServicePackageRepository repository, ImageStore imageStore) {
        this.repository = repository;
        this.imageStore = imageStore;
    }

    public List<PackageDto> findAll() {
        return repository.findAllByOrderByOrderAsc().stream().map(this::toDto).toList();
    }

    public List<PackageDto> findFeatured() {
        return repository.findByFeaturedTrueOrderByOrderAsc().stream().map(this::toDto).toList();
    }

    public Optional<PackageDto> findBySlug(String slug) {
        return repository.findBySlug(slug).map(this::toDto);
    }

    private PackageDto toDto(ServicePackage p) {
        return new PackageDto(
                p.getId(), p.getSlug(), p.getName(), p.getTagline(), p.getOrder(),
                p.getPriceType(), p.getPriceFrom(), p.getPriceNote(),
                p.getSummary(), p.getIncludes(), p.getAddOns(),
                p.getTimeline(), p.getBestFor(),
                p.getImageKey() == null ? null : imageStore.urlFor(p.getImageKey()),
                p.getThumbKey() == null ? null : imageStore.urlFor(p.getThumbKey()),
                p.getLqip(), p.isFeatured(), p.getCtaLabel()
        );
    }
}
