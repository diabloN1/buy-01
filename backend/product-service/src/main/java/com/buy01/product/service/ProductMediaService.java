package com.buy01.product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.buy01.product.DTOs.MediaResponse;
import com.buy01.product.client.MediaClient;
import com.buy01.product.exception.custom.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductMediaService {

    private final MediaClient mediaClient;

    public List<String> uploadImages(List<MultipartFile> images, String productId) {

        // Could be removed since we already validat the image existance in the
        // controller using the exception handler "MissingServletRequestPartException"
        if (images == null || images.isEmpty()) {
            throw new BadRequestException("At least one image is required.");
        }

        List<String> imageIds = new ArrayList<>();

        for (MultipartFile image : images) {
            System.out.println("===========");
            System.out.println(image.getOriginalFilename());
            System.out.println(image.getContentType());
            System.out.println(image.getSize());
            System.out.println(image.isEmpty());
            MediaResponse media = mediaClient.upload(image, productId);
            imageIds.add(media.id());
        }

        return imageIds;
    }
}