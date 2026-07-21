package com.buy01.media.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@Component
@RequiredArgsConstructor
public class MinioBucketInitializer {

    private final S3Client s3Client;

    @Value("${minio.bucket}")
    private String bucket;

    @PostConstruct
    public void init() {

        boolean exists = s3Client.listBuckets()
                .buckets()
                .stream()
                .anyMatch(b -> b.name().equals(bucket));

        if (!exists) {
            s3Client.createBucket(
                    CreateBucketRequest.builder()
                            .bucket(bucket)
                            .build()
            );
        }
    }
}