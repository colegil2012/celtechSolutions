package com.celtech.api.clients.repository.data;

import com.celtech.api.clients.model.data.ClientInquiry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClientInquiryRepository extends MongoRepository<ClientInquiry, String> {
    List<ClientInquiry> findBySiteIdOrderByCreatedAtDesc(String siteId);
    long countBySiteIdAndStatus(String siteId, ClientInquiry.Status status);
}