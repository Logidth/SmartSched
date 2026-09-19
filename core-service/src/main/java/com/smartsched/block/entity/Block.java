package com.smartsched.block.entity;

import com.smartsched.common.entity.BaseEntity;
import com.smartsched.common.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "blocks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        }
)
public class Block extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 250)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

}