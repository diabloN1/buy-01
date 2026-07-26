package com.buy01.audit.repository;


import com.buy01.audit.entity.ProductAudit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductAuditRepo extends MongoRepository<ProductAudit, String> {
    
}
