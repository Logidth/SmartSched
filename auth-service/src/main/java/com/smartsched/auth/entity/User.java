package com.smartsched.auth.entity;

import com.smartsched.common.entity.BaseEntity;
import com.smartsched.common.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.smartsched.branch.entity.Branch;
import com.smartsched.faculty.entity.Faculty;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean accountLocked = false;

    @Column(nullable = false)
    private boolean firstLogin = true;
}