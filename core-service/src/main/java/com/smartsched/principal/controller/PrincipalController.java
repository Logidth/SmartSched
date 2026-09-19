package com.smartsched.principal.controller;

import com.smartsched.principal.dto.CreateAdminRequest;
import com.smartsched.principal.dto.CreateHodRequest;
import com.smartsched.principal.dto.ResetPasswordRequest;
import com.smartsched.principal.dto.UserResponse;
import com.smartsched.principal.service.PrincipalUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/principal/users")
@RequiredArgsConstructor
public class PrincipalController {

    private final PrincipalUserService principalUserService;

    @PostMapping("/department-admin")
    public ResponseEntity<UserResponse> createDepartmentAdmin(
            @Valid @RequestBody CreateAdminRequest request) {

        return ResponseEntity.ok(
                principalUserService.createDepartmentAdmin(request)
        );
    }

    @PostMapping("/hod")
    public ResponseEntity<UserResponse> createHod(
            @Valid @RequestBody CreateHodRequest request) {

        return ResponseEntity.ok(
                principalUserService.createHod(request)
        );
    }

    @GetMapping("/department-admin")
    public ResponseEntity<List<UserResponse>> getAdmins() {

        return ResponseEntity.ok(
                principalUserService.getAdmins()
        );
    }

    @GetMapping("/hod")
    public ResponseEntity<List<UserResponse>> getHods() {

        return ResponseEntity.ok(
                principalUserService.getHods()
        );
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<String> enableUser(
            @PathVariable Long id) {

        principalUserService.enableUser(id);

        return ResponseEntity.ok("User Enabled");
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<String> disableUser(
            @PathVariable Long id) {

        principalUserService.disableUser(id);

        return ResponseEntity.ok("User Disabled");
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<String> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordRequest request) {

        principalUserService.resetPassword(id, request);

        return ResponseEntity.ok("Password Reset Successfully");
    }


}