package com.buy01.media.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.buy01.media.entity.Media;

public interface MediaRepository extends MongoRepository<Media, String> {
    Page<Media> findByProductId(String productId, Pageable pageable);

    Page<Media> findByUserId(String userId, Pageable pageable);
}
