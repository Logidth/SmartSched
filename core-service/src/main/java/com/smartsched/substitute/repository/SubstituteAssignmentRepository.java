package com.smartsched.substitute.repository;

import com.smartsched.faculty.entity.Faculty;
import com.smartsched.lecture.entity.LectureLog;
import com.smartsched.substitute.entity.SubstituteAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubstituteAssignmentRepository
        extends JpaRepository<SubstituteAssignment, Long> {

    List<SubstituteAssignment> findBySubstituteFaculty(
            Faculty faculty
    );

    List<SubstituteAssignment> findByOriginalFaculty(
            Faculty faculty
    );

    Optional<SubstituteAssignment> findByLectureLog(
            LectureLog lectureLog
    );
}