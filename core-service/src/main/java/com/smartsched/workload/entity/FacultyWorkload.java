package com.smartsched.workload.entity;

import com.smartsched.common.entity.BaseEntity;
import com.smartsched.faculty.entity.Faculty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "faculty_workloads")
public class FacultyWorkload extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(nullable = false)
    private Integer theoryHours = 0;

    @Column(nullable = false)
    private Integer labHours = 0;

    @Column(nullable = false)
    private Integer totalHours = 0;
}