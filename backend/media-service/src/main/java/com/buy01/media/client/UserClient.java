package com.buy01.media.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @DeleteMapping("/users/me/avatar")
    public ResponseEntity<Void> deleteAvatar();
}