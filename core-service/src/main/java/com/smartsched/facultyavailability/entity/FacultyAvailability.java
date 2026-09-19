package com.smartsched.facultyavailability.entity;

import com.smartsched.faculty.entity.Faculty;
import com.smartsched.scheduler.enums.WorkingDay;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faculty_availability")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @Enumerated(EnumType.STRING)
    private WorkingDay day;

    private Integer period;

    private Boolean available;
}