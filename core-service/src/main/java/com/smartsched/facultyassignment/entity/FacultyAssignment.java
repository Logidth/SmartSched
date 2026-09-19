package com.smartsched.facultyassignment.entity;

import com.smartsched.common.entity.BaseEntity;
import com.smartsched.curriculumsubject.entity.CurriculumSubject;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.studentclass.entity.StudentClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "faculty_assignments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "student_class_id",
                        "curriculum_subject_id"
                })
        }
)
public class FacultyAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_class_id", nullable = false)
    private StudentClass studentClass;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curriculum_subject_id", nullable = false)
    private CurriculumSubject curriculumSubject;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(nullable = false)
    private Integer priority = 1;

}