package com.example.orderupdate.client;

import com.example.orderupdate.security.TokenValidationRequest;
import com.example.orderupdate.security.TokenValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "${auth.service.url}")
public interface AuthServiceClient {

    @PostMapping("${auth.service.validate-endpoint}")
    TokenValidationResponse validateToken(@RequestBody TokenValidationRequest request);
}
