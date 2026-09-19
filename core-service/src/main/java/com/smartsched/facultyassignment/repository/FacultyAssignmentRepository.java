package com.smartsched.facultyassignment.repository;

import com.smartsched.curriculumsubject.entity.CurriculumSubject;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.facultyassignment.entity.FacultyAssignment;
import com.smartsched.studentclass.entity.StudentClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FacultyAssignmentRepository
        extends JpaRepository<FacultyAssignment, Long> {

    /**
     * All assignments belonging to a particular student class.
     */
    List<FacultyAssignment> findByStudentClass(StudentClass studentClass);

    /**
     * All assignments handled by a faculty member.
     */
    List<FacultyAssignment> findByFaculty(Faculty faculty);

    /**
     * Find the assignment for one class + one curriculum subject.
     */
    Optional<FacultyAssignment> findByStudentClassAndCurriculumSubject(
            StudentClass studentClass,
            CurriculumSubject curriculumSubject
    );

    /**
     * Duplicate validation.
     */
    boolean existsByStudentClassAndCurriculumSubject(
            StudentClass studentClass,
            CurriculumSubject curriculumSubject
    );

    /**
     * All class assignments for a curriculum subject.
     *
     * Important:
     * There is deliberately NO branch restriction here.
     *
     * A faculty member from VLSI can therefore be assigned to
     * a CSE class, provided an explicit FacultyAssignment exists.
     */
    List<FacultyAssignment> findByCurriculumSubject(
            CurriculumSubject curriculumSubject
    );

    /**
     * Faculty workload.
     */
    long countByFaculty(Faculty faculty);

    long countByFacultyId(Long facultyId);

    /**
     * Number of distinct classes handled by faculty.
     */
    @Query("""
        SELECT COUNT(DISTINCT fa.studentClass.id)
        FROM FacultyAssignment fa
        WHERE fa.faculty.id = :facultyId
    """)
    long countDistinctStudentClassByFacultyId(Long facultyId);

    /**
     * Number of distinct classes handled by faculty.
     */
    @Query("""
        SELECT COUNT(DISTINCT fa.studentClass.id)
        FROM FacultyAssignment fa
        WHERE fa.faculty.id = :facultyId
    """)
    long countClasses(Long facultyId);
}