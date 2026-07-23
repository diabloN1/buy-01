package com.buy01.media.controller;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.buy01.media.entity.Media;
import com.buy01.media.service.MediaServiceImpl;

import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/media/images")
@RequiredArgsConstructor
public class MediaController {

        private final MediaServiceImpl mediaService;

        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
        public ResponseEntity<Media> upload(
                        @RequestPart("image") MultipartFile image,
                        @RequestParam(required = false) String productId,
                        @RequestParam(required = false) String userId) throws IOException {

                Media media = mediaService.upload(image, productId, userId);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(media);
        }

        @GetMapping("/{id}")
        public ResponseEntity<Resource> get(@PathVariable String id) {
                Media media = mediaService.get(id);
                Resource resource = mediaService.download(id);

                return ResponseEntity.ok()
                                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)))
                                .contentType(MediaType.parseMediaType(media.getContentType()))
                                .body(resource);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
        public ResponseEntity<Void> delete(@PathVariable String id) {
                mediaService.delete(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/count")
        @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
        public ResponseEntity<Long> countMedia() {
                return ResponseEntity.ok(mediaService.countMedia());
        }
}