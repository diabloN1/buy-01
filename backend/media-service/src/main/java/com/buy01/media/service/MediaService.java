package com.buy01.media.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.buy01.media.entity.Media;
import org.springframework.core.io.Resource;

public interface MediaService {

    Media get(String id);

    Media upload(MultipartFile file, String productId) throws IOException;

    Resource download(String id);

    void delete(String id);

    long countMedia();
}