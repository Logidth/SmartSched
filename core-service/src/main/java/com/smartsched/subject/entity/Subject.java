package com.smartsched.subject.entity;

import com.smartsched.common.entity.BaseEntity;
import com.smartsched.common.enums.Status;
import com.smartsched.common.enums.SubjectCategory;
import com.smartsched.common.enums.SubjectType;
import com.smartsched.regulation.entity.Regulation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "subjects",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "subject_code",
                        "regulation_id"
                })
        }
)
public class Subject extends BaseEntity {

    @Column(name = "subject_code", nullable = false, length = 20)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regulation_id", nullable = false)
    private Regulation regulation;

    @Column(nullable = false)
    private Integer credits;

    @Column(nullable = false)
    private Integer theoryHours;

    @Column(nullable = false)
    private Integer labHours;

    @Column(nullable = false)
    private Integer totalHours;

    /**
     * Number of periods/hours this subject should be scheduled for
     * PER WEEK in the timetable. This is distinct from totalHours
     * (theoryHours + labHours), which represents the overall subject
     * load used for credits/reporting. The scheduler (WorkloadBuilder)
     * must use hoursPerWeek, NOT totalHours, when computing how many
     * hours remain to be allocated for a faculty-subject assignment.
     */
    @Column(name = "hours_per_week", nullable = false)
    private Integer hoursPerWeek;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubjectType subjectType;

    @Column(nullable = false)
    private Boolean requiresLabRoom = false;
    @Column(nullable = false)
    private Integer continuousPeriods = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubjectCategory subjectCategory;

}