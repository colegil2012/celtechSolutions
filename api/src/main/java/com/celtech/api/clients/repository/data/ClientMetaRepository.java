package com.celtech.api.clients.repository.data;

import com.celtech.api.clients.model.data.ClientMeta;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ClientMetaRepository extends MongoRepository<ClientMeta, String> {
    Optional<ClientMeta> findBySiteId(String siteId);
}