package com.__buy.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.__buy.user_service.dto.MediaResponse;

@FeignClient(name = "MEDIA-SERVICE")
public interface MediaClient {

    @PostMapping(value = "/media/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    MediaResponse upload(
            @RequestPart("image") MultipartFile image,
            @RequestParam(required = false) String productId);

    @DeleteMapping("/media/images/{id}")
    void delete(@PathVariable String id);
}