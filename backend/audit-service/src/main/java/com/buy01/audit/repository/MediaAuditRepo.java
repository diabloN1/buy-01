package com.buy01.audit.repository;


import com.buy01.audit.entity.MediaAudit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaAuditRepo extends MongoRepository<MediaAudit, String> {
    
}
