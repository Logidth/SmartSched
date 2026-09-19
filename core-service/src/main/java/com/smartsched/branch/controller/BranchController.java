package com.smartsched.branch.controller;

import com.smartsched.branch.dto.BranchRequest;
import com.smartsched.branch.dto.BranchResponse;
import com.smartsched.branch.service.BranchService;
import com.smartsched.common.response.ApiResponse;
import com.smartsched.common.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(
            @Valid @RequestBody BranchRequest request) {

        BranchResponse response = branchService.createBranch(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success("Branch created successfully.", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponse>> updateBranch(
            @PathVariable Long id,
            @Valid @RequestBody BranchRequest request) {

        BranchResponse response = branchService.updateBranch(id, request);

        return ResponseEntity.ok(
                ResponseUtil.success("Branch updated successfully.", response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranch(
            @PathVariable Long id) {

        BranchResponse response = branchService.getBranch(id);

        return ResponseEntity.ok(
                ResponseUtil.success("Branch retrieved successfully.", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getAllBranches() {

        List<BranchResponse> response = branchService.getAllBranches();

        return ResponseEntity.ok(
                ResponseUtil.success("Branches retrieved successfully.", response)
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<BranchResponse>> activateBranch(
            @PathVariable Long id) {

        BranchResponse response = branchService.activateBranch(id);

        return ResponseEntity.ok(
                ResponseUtil.success("Branch activated successfully.", response)
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<BranchResponse>> deactivateBranch(
            @PathVariable Long id) {

        BranchResponse response = branchService.deactivateBranch(id);

        return ResponseEntity.ok(
                ResponseUtil.success("Branch deactivated successfully.", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBranch(
            @PathVariable Long id) {

        branchService.deleteBranch(id);

        return ResponseEntity.ok(
                ResponseUtil.success("Branch deleted successfully.", null)
        );
    }
}