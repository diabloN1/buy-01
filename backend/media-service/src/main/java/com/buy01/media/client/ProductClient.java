package com.buy01.media.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

        @DeleteMapping("/products/{productId}/images/{imageId}")
        ResponseEntity<Void> removeImageFromProduct(
                        @PathVariable String productId,
                        @PathVariable String imageId);

}
