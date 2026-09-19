package com.smartsched.notification.service;

import com.smartsched.auth.entity.User;
import com.smartsched.notification.dto.NotificationResponse;
import com.smartsched.notification.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    /**
     * Creates a notification for the given recipient. Called
     * internally by other services (e.g. the scheduler, when a HOD
     * approves/rejects a timetable) - there is no public "create"
     * endpoint, notifications are always a side effect of some other
     * action.
     */
    void notify(
            User recipient,
            NotificationType type,
            String title,
            String message,
            Long studentClassId,
            Long academicYearId,
            String className
    );

    List<NotificationResponse> getMyNotifications();

    long getUnreadCount();

    void markAsRead(Long notificationId);

    void markAllAsRead();
}
