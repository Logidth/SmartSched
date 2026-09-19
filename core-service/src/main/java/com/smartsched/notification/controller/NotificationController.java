package com.smartsched.notification.controller;

import com.smartsched.common.response.ApiResponse;
import com.smartsched.notification.dto.NotificationResponse;
import com.smartsched.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Shared across every role (ADMIN, HOD, PRINCIPAL, FACULTY) - each
 * caller only ever sees/affects their OWN notifications, resolved
 * server-side from the auth token, so no role restriction is needed
 * beyond "must be logged in" (already enforced by SecurityConfig's
 * anyRequest().authenticated() fallback).
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> myNotifications() {

        return new ApiResponse<>(
                true,
                "Notifications Retrieved Successfully",
                notificationService.getMyNotifications(),
                LocalDateTime.now()
        );
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> unreadCount() {

        return new ApiResponse<>(
                true,
                "Unread Notification Count Retrieved Successfully",
                notificationService.getUnreadCount(),
                LocalDateTime.now()
        );
    }

    @PutMapping("/{id}/read")
    public ApiResponse<String> markAsRead(@PathVariable Long id) {

        notificationService.markAsRead(id);

        return new ApiResponse<>(
                true,
                "Notification Marked As Read",
                "SUCCESS",
                LocalDateTime.now()
        );
    }

    @PutMapping("/read-all")
    public ApiResponse<String> markAllAsRead() {

        notificationService.markAllAsRead();

        return new ApiResponse<>(
                true,
                "All Notifications Marked As Read",
                "SUCCESS",
                LocalDateTime.now()
        );
    }
}
