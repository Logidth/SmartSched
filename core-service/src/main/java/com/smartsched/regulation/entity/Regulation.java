package com.smartsched.regulation.entity;

import com.smartsched.common.entity.BaseEntity;
import com.smartsched.common.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="regulations")
public class Regulation extends BaseEntity {

    @Column(nullable=false,unique=true,length=20)
    private String code;

    @Column(length=255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Status status=Status.ACTIVE;
}