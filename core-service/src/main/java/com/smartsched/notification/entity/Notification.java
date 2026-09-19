package com.smartsched.notification.entity;

import com.smartsched.auth.entity.User;
import com.smartsched.common.entity.BaseEntity;
import com.smartsched.notification.enums.NotificationType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    /**
     * The user this notification is for (e.g. the department Admin
     * who submitted the timetable that the HOD just approved/rejected).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NotificationType type;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    /**
     * Optional pointers back to the timetable this notification is
     * about, so the frontend can deep-link to it. Kept as plain
     * columns (not a FK/relationship) since timetable entries can be
     * deleted/regenerated independently of notification history.
     */
    private Long studentClassId;

    private Long academicYearId;

    private String className;

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private boolean read = false;
}
