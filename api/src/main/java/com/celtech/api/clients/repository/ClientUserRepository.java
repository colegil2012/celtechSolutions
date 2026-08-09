package com.celtech.api.clients.repository;

import com.celtech.api.clients.model.ClientUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ClientUserRepository extends MongoRepository<ClientUser, String> {
    Optional<ClientUser> findByEmailIgnoreCase(String email);
}
