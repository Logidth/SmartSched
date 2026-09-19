package com.smartsched.room.controller;

import com.smartsched.common.response.ApiResponse;
import com.smartsched.common.util.ResponseUtil;
import com.smartsched.room.dto.RoomRequest;
import com.smartsched.room.dto.RoomResponse;
import com.smartsched.room.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> create(
            @Valid @RequestBody RoomRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        "Room created successfully.",
                        roomService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Room updated successfully.",
                        roomService.update(id, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Room retrieved successfully.",
                        roomService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAll() {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Rooms retrieved successfully.",
                        roomService.getAll()));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getByBranch(
            @PathVariable Long branchId) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Rooms retrieved successfully.",
                        roomService.getByBranch(branchId)));
    }

    @GetMapping("/type/{roomType}")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getByRoomType(
            @PathVariable String roomType) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Rooms retrieved successfully.",
                        roomService.getByRoomType(roomType)));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<RoomResponse>> activate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Room activated successfully.",
                        roomService.activate(id)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<RoomResponse>> deactivate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Room deactivated successfully.",
                        roomService.deactivate(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        roomService.delete(id);

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Room deleted successfully.",
                        null));
    }

}