package com.smartsched.auth.controller;

import com.smartsched.auth.dto.ChangePasswordRequest;
import com.smartsched.auth.dto.LoginRequest;
import com.smartsched.auth.dto.LoginResponse;
import com.smartsched.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authenticationService.login(request);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        authenticationService.changePassword(request);

        return ResponseEntity.ok("Password Changed Successfully");
    }

}