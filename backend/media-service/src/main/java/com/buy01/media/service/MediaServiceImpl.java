package com.buy01.media.service;

import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.buy01.media.entity.Media;
import com.buy01.media.exception.custom.BadRequestException;
import com.buy01.media.exception.custom.NotFoundException;
import com.buy01.media.repository.MediaRepository;

import lombok.RequiredArgsConstructor;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

        private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
        private final MediaRepository mediaRepository;
        private final S3Client s3Client;

        @Value("${minio.bucket}")
        private String bucket;

        @Override
        public Media get(String id) {

                return mediaRepository.findById(id)
                                .orElseThrow(
                                                () -> new NotFoundException("Image not found"));
        }

        @Override
        public Media upload(
                        MultipartFile file,
                        String productId,
                        String userId) throws IOException {

                validate(file);

                String objectName = UUID.randomUUID()
                                + getExtension(file.getOriginalFilename());

                PutObjectRequest request = PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(objectName)
                                .contentType(file.getContentType())
                                .build();

                s3Client.putObject(
                                request,
                                RequestBody.fromInputStream(
                                                file.getInputStream(),
                                                file.getSize()));

                Media media = Media.builder()
                                .path(objectName)
                                .productId(productId)
                                .userId(userId)
                                .contentType(file.getContentType())
                                .build();

                return mediaRepository.save(media);
        }

        @Override
        public Resource download(String id) {
                Media media = mediaRepository.findById(id)
                                .orElseThrow(
                                                () -> new NotFoundException("Image not found"));

                ResponseInputStream<GetObjectResponse> stream = s3Client.getObject(
                                GetObjectRequest.builder()
                                                .bucket(bucket)
                                                .key(media.getPath())
                                                .build());

                return new InputStreamResource(stream);
        }

        @Override
        public void delete(String id) {
                Media media = mediaRepository.findById(id)
                                .orElseThrow(
                                                () -> new NotFoundException("Image not found"));

                s3Client.deleteObject(
                                DeleteObjectRequest.builder()
                                                .bucket(bucket)
                                                .key(media.getPath())
                                                .build());

                mediaRepository.delete(media);
        }

        private void validate(MultipartFile file) throws IOException {
                if (file.isEmpty()) {
                        throw new BadRequestException(
                                        "File is empty.");
                }

                if (file.getSize() > MAX_FILE_SIZE) {
                        throw new BadRequestException(
                                        "Maximum size is 2 MB.");
                }

                String contentType = file.getContentType();
                if (contentType == null ||
                                !contentType.startsWith("image/")) {

                        throw new BadRequestException(
                                        "Only images are allowed.");
                }

                // System.out.println("======= MEDIA DATDA WIW ========");
                // System.out.println(file.getOriginalFilename());
                // System.out.println(file.getContentType());
                // System.out.println(file.getSize());
                // System.out.println(file.isEmpty());

                // BufferedImage preview = ImageIO.read(file.getInputStream());
                // System.out.println(preview);

                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image == null) {
                        throw new BadRequestException(
                                        "Invalid image.");
                }
        }

        private String getExtension(String filename) {
                if (filename == null || !filename.contains(".")) {
                        return "";
                }

                return filename.substring(
                                filename.lastIndexOf('.'));
        }

        @Override
        public long countMedia() {
                return mediaRepository.count();
        }
}