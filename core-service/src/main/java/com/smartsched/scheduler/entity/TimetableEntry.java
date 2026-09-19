package com.smartsched.scheduler.entity;

import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.lecture.entity.LectureLog;
import com.smartsched.room.entity.Room;
import com.smartsched.scheduler.enums.TimetableStatus;
import com.smartsched.scheduler.enums.WorkingDay;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.subject.entity.Subject;

import jakarta.persistence.*;
import lombok.*;
import com.smartsched.auth.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity

@Table(
        name = "timetable_entries",
        uniqueConstraints = {

                @UniqueConstraint(columnNames = {
                        "student_class_id",
                        "academic_year_id",
                        "day",
                        "period_number"
                }),

                @UniqueConstraint(columnNames = {
                        "faculty_id",
                        "academic_year_id",
                        "day",
                        "period_number"
                }),

                @UniqueConstraint(columnNames = {
                        "room_id",
                        "academic_year_id",
                        "day",
                        "period_number"
                })

        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_class_id", nullable = false)
    private StudentClass studentClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkingDay day;

    @Column(nullable = false)
    private Integer periodNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;
    @Builder.Default
    @Column(nullable = false)
    private Boolean approved = false;

    private LocalDateTime approvedAt;

    private String remarks;
    @Builder.Default
    @Column(nullable = false)
    private Boolean cancelled = false;
    @Builder.Default
    @Column(nullable = false)
    private Boolean progressUpdated = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimetableStatus status = TimetableStatus.DRAFT;
    @OneToMany(
            mappedBy = "timetableEntry",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LectureLog> lectureLogs = new ArrayList<>();



    @ManyToOne
    @JoinColumn(name = "approved_by_hod")
    private User approvedByHod;

    private LocalDateTime hodApprovedAt;

    @Column(length = 500)
    private String hodRemarks;

    /**
     * Timestamp of the HOD's decision (approve OR reject), set every
     * time hodApprove()/hodReject() runs. Unlike hodApprovedAt (which
     * is only ever populated on an approval, and is nulled back out
     * on a later rejection), this field always reflects "when did the
     * HOD last act on this timetable" - it is what the HOD's
     * Approved/Rejected history screens sort and display by.
     */
    private LocalDateTime hodDecisionAt;

}