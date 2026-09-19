package com.smartsched.substitute.entity;

import com.smartsched.common.entity.BaseEntity;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.lecture.entity.LectureLog;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "substitute_assignments")
public class SubstituteAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_log_id", nullable = false)
    private LectureLog lectureLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_faculty_id", nullable = false)
    private Faculty originalFaculty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substitute_faculty_id", nullable = false)
    private Faculty substituteFaculty;

    @Column(nullable = false)
    private LocalDate assignmentDate;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false)
    private Boolean accepted = false;

    @Column(nullable = false)
    private LocalDateTime assignedAt;
}