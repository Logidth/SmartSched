package com.smartsched.academicyear.entity;

import com.smartsched.common.entity.BaseEntity;
import com.smartsched.common.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "academic_years")
public class AcademicYear extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;
}