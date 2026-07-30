package com.celtech.api.repository;

import com.celtech.api.model.Entry;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EntryRepository extends MongoRepository<Entry, String> {

    List<Entry> findByKind(String kind);

    List<Entry> findByKindAndFeaturedTrue(String kind);

    List<Entry> findByCategoryContaining(String category);

    /** Random sample within a kind, for the homepage "Our Work"/"Our Friends" strips. */
    @Aggregation(pipeline = {
        "{ $match: { kind: ?0 } }",
        "{ $sample: { size: ?1 } }"
    })
    List<Entry> findRandomByKind(String kind, int size);
}
