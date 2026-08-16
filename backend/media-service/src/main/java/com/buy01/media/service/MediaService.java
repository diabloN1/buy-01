package com.buy01.media.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.buy01.media.entity.Media;
import org.springframework.core.io.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MediaService {

    Media get(String id);

    Media upload(MultipartFile file, String productId) throws IOException;

    Resource download(String id);

    void delete(String id, boolean fromService);

    long countMedia();

    Page<Media> getMediaByUserId(String userId, Pageable pageable);
}