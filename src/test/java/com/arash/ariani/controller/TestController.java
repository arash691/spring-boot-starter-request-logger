package com.arash.ariani.controller;

import com.arash.ariani.annotation.LogRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @LogRequest
    @PostMapping("/user")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        UserResponse response = new UserResponse(
            request.name(),
            "token-" + request.name(),
            "user created successfully"
        );
        return ResponseEntity.ok(response);
    }

    @LogRequest(
        maskPatterns = {"password:.*:***", "token:.*:MASKED-TOKEN"},
        excludeHeaders = {"X-Secret-Header"}
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = new LoginResponse(
            "token-" + request.username(),
            "login successful"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("public data");
    }

    public record UserRequest(String name, String email, String password) {}
    public record UserResponse(String name, String token, String message) {}
    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String token, String message) {}
} 