package com.smartsched.faculty.entity;

import com.smartsched.auth.entity.User;
import com.smartsched.branch.entity.Branch;
import com.smartsched.common.entity.BaseEntity;
import com.smartsched.common.enums.FacultyDesignation;
import com.smartsched.common.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "faculties")
public class Faculty extends BaseEntity {

    @Column(nullable = false)
    private String employeeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    private FacultyDesignation designation;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private Integer maxWeeklyHours = 18;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}