package com.smartsched.curriculum.entity;

import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.branch.entity.Branch;
import com.smartsched.common.entity.BaseEntity;
import com.smartsched.common.enums.Status;
import com.smartsched.regulation.entity.Regulation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "curriculums",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "branch_id",
                        "regulation_id",
                        "academic_year_id"
                })
        }
)
public class Curriculum extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regulation_id", nullable = false)
    private Regulation regulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

}