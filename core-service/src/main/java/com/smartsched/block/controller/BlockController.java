package com.smartsched.block.controller;

import com.smartsched.block.dto.BlockRequest;
import com.smartsched.block.dto.BlockResponse;
import com.smartsched.block.service.BlockService;
import com.smartsched.common.response.ApiResponse;
import com.smartsched.common.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    @PostMapping
    public ResponseEntity<ApiResponse<BlockResponse>> create(
            @Valid @RequestBody BlockRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        "Block created successfully.",
                        blockService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BlockResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody BlockRequest request) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Block updated successfully.",
                        blockService.update(id, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BlockResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Block retrieved successfully.",
                        blockService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BlockResponse>>> getAll() {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Blocks retrieved successfully.",
                        blockService.getAll()));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<BlockResponse>> activate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Block activated successfully.",
                        blockService.activate(id)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<BlockResponse>> deactivate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Block deactivated successfully.",
                        blockService.deactivate(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        blockService.delete(id);

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Block deleted successfully.",
                        null));
    }
}