package com.smartsched.studentclass.entity;

import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.branch.entity.Branch;
import com.smartsched.common.entity.BaseEntity;
import com.smartsched.common.enums.Status;
import com.smartsched.regulation.entity.Regulation;
import com.smartsched.room.entity.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "student_classes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "academic_year_id",
                        "regulation_id",
                        "branch_id",
                        "year",
                        "semester",
                        "section"
                })
        }
)
public class StudentClass extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false, length = 5)
    private String section;

    @Column(nullable = false)
    private Integer strength;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "regulation_id", nullable = false)
    private Regulation regulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_room_id")
    private Room preferredRoom;

}