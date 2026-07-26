package com.buy01.audit.repository;


import com.buy01.audit.entity.UserAudit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserAuditRepo extends MongoRepository<UserAudit, String> {
    
}
