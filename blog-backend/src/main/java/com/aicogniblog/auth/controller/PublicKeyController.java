package com.aicogniblog.auth.controller;

import com.aicogniblog.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PublicKeyController {

    @Value("${rsa.public-key}")
    private String publicKey;

    @GetMapping("/public-key")
    public Result<Map<String, String>> getPublicKey() {
        return Result.success(Map.of("publicKey", publicKey));
    }
}

