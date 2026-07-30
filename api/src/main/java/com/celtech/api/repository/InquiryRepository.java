package com.celtech.api.repository;

import com.celtech.api.model.Inquiry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InquiryRepository extends MongoRepository<Inquiry, String> {
    List<Inquiry> findByStatusOrderByCreatedAtDesc(Inquiry.Status status);
}
