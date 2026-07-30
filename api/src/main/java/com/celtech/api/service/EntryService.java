package com.celtech.api.service;

import com.celtech.api.dto.EntryDto;
import com.celtech.api.model.Entry;
import com.celtech.api.repository.EntryRepository;
import com.celtech.api.storage.ImageStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntryService {

    private final EntryRepository repository;
    private final ImageStore imageStore;

    public EntryService(EntryRepository repository, ImageStore imageStore) {
        this.repository = repository;
        this.imageStore = imageStore;
    }

    public List<EntryDto> findByKind(String kind) {
        return repository.findByKind(kind).stream().map(this::toDto).toList();
    }

    public List<EntryDto> findFeatured(String kind) {
        return repository.findByKindAndFeaturedTrue(kind).stream().map(this::toDto).toList();
    }

    public List<EntryDto> findRandom(String kind, int limit) {
        int safe = Math.clamp(limit, 1, 24);
        return repository.findRandomByKind(kind, safe).stream().map(this::toDto).toList();
    }

    public List<EntryDto> findByCategory(String category) {
        return repository.findByCategoryContaining(category).stream().map(this::toDto).toList();
    }

    public Optional<EntryDto> findById(String id) {
        return repository.findById(id).map(this::toDto);
    }

    private EntryDto toDto(Entry e) {
        return new EntryDto(
                e.getId(), e.getKind(), e.getName(), e.getUrl(),
                e.getBlurb(), e.getSummary(), e.getCategory(), e.getStack(),
                e.isBuiltByUs(),
                e.getImageKey() == null ? null : imageStore.urlFor(e.getImageKey()),
                e.getThumbKey() == null ? null : imageStore.urlFor(e.getThumbKey()),
                e.getLqip(), e.isFeatured(), e.getLaunchedYear()
        );
    }
}
