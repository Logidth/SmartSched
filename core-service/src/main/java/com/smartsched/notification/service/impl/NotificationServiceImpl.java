package com.smartsched.notification.service.impl;

import com.smartsched.auth.entity.User;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.notification.dto.NotificationResponse;
import com.smartsched.notification.entity.Notification;
import com.smartsched.notification.enums.NotificationType;
import com.smartsched.notification.repository.NotificationRepository;
import com.smartsched.notification.service.NotificationService;
import com.smartsched.security.service.CurrentUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public void notify(
            User recipient,
            NotificationType type,
            String title,
            String message,
            Long studentClassId,
            Long academicYearId,
            String className
    ) {

        if (recipient == null) {
            // No one to notify (e.g. branch has no Admin assigned
            // yet) - silently skip rather than failing the caller's
            // approve/reject action, which already succeeded.
            return;
        }

        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .message(message)
                .studentClassId(studentClassId)
                .academicYearId(academicYearId)
                .className(className)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {

        User currentUser = currentUserService.getCurrentUser();

        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {

        User currentUser = currentUserService.getCurrentUser();

        return notificationRepository
                .countByRecipientIdAndReadFalse(currentUser.getId());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {

        User currentUser = currentUserService.getCurrentUser();

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found"
                        )
                );

        // A user should only ever be able to mark their OWN
        // notifications as read.
        if (!notification.getRecipient().getId().equals(currentUser.getId())) {

            throw new ResourceNotFoundException(
                    "Notification not found"
            );
        }

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {

        User currentUser = currentUserService.getCurrentUser();

        List<Notification> unread =
                notificationRepository
                        .findByRecipientIdAndReadFalseOrderByCreatedAtDesc(
                                currentUser.getId()
                        );

        if (unread.isEmpty()) {
            return;
        }

        unread.forEach(n -> n.setRead(true));

        notificationRepository.saveAll(unread);
    }

    private NotificationResponse map(Notification notification) {

        return NotificationResponse.builder()

                .id(notification.getId())

                .type(notification.getType().name())

                .title(notification.getTitle())

                .message(notification.getMessage())

                .studentClassId(notification.getStudentClassId())

                .academicYearId(notification.getAcademicYearId())

                .className(notification.getClassName())

                .read(notification.isRead())

                .createdAt(notification.getCreatedAt())

                .build();
    }
}
