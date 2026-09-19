package com.smartsched.notification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private Long id;

    private String type;

    private String title;

    private String message;

    private Long studentClassId;

    private Long academicYearId;

    private String className;

    private boolean read;

    private LocalDateTime createdAt;
}
