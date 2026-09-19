package com.smartsched.curriculumsubject.entity;

import com.smartsched.common.entity.BaseEntity;
import com.smartsched.common.enums.Status;
import com.smartsched.curriculum.entity.Curriculum;
import com.smartsched.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "curriculum_subjects",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "curriculum_id",
                        "subject_id"
                })
        }
)
public class CurriculumSubject extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false)
    private Curriculum curriculum;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false)
    private Integer displayOrder;

    /**
     * Optional override for the number of hours/week this subject
     * should be scheduled for in THIS curriculum context. When null,
     * the scheduler falls back to Subject.hoursPerWeek. Distinct from
     * Subject.totalHours (theoryHours + labHours).
     */
    @Column(name = "hours_per_week")
    private Integer hoursPerWeek;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    /**
     * Effective weekly hours to schedule for this curriculum subject:
     * this entity's override if present, otherwise the parent
     * Subject's hoursPerWeek.
     */
    public Integer resolveHoursPerWeek() {

        if (this.hoursPerWeek != null) {
            return this.hoursPerWeek;
        }

        return this.subject != null
                ? this.subject.getHoursPerWeek()
                : null;
    }
}