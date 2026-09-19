package com.smartsched.facultyassignment.service.impl;

import com.smartsched.common.exception.BadRequestException;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.curriculum.entity.Curriculum;
import com.smartsched.curriculum.repository.CurriculumRepository;
import com.smartsched.curriculumsubject.dto.CurriculumSubjectResponse;
import com.smartsched.curriculumsubject.entity.CurriculumSubject;
import com.smartsched.curriculumsubject.mapper.CurriculumSubjectMapper;
import com.smartsched.curriculumsubject.repository.CurriculumSubjectRepository;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.faculty.repository.FacultyRepository;
import com.smartsched.facultyassignment.dto.CreateFacultyAssignmentRequest;
import com.smartsched.facultyassignment.dto.FacultyAssignmentResponse;
import com.smartsched.facultyassignment.entity.FacultyAssignment;
import com.smartsched.facultyassignment.mapper.FacultyAssignmentMapper;
import com.smartsched.facultyassignment.repository.FacultyAssignmentRepository;
import com.smartsched.facultyassignment.service.FacultyAssignmentService;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.studentclass.repository.StudentClassRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class FacultyAssignmentServiceImpl
        implements FacultyAssignmentService {

    private final FacultyAssignmentRepository assignmentRepository;

    private final StudentClassRepository studentClassRepository;

    private final FacultyRepository facultyRepository;

    private final CurriculumSubjectRepository curriculumSubjectRepository;

    private final CurriculumRepository curriculumRepository;

    private final FacultyAssignmentMapper mapper;

    private final CurriculumSubjectMapper curriculumSubjectMapper;

    // ------------------------------------------------
    // Helper methods
    // ------------------------------------------------

    private StudentClass getStudentClass(Long id) {

        return studentClassRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student Class Not Found"
                        )
                );
    }

    private Faculty getFaculty(Long id) {

        return facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Faculty Not Found"
                        )
                );
    }

    private CurriculumSubject getCurriculumSubject(Long id) {

        return curriculumSubjectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Curriculum Subject Not Found"
                        )
                );
    }

    // ------------------------------------------------
    // Get Curriculum Subjects For Student Class
    // ------------------------------------------------

    @Override
    public List<CurriculumSubjectResponse> getCurriculumSubjectsForClass(
            Long studentClassId) {

        // 1. Get the student class
        StudentClass studentClass = getStudentClass(studentClassId);

        // 2. Get the curriculum for this class
        //    (Branch + Regulation + AcademicYear)
        Curriculum curriculum = curriculumRepository
                .findByBranchAndRegulationAndAcademicYear(
                        studentClass.getBranch(),
                        studentClass.getRegulation(),
                        studentClass.getAcademicYear()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Curriculum not found for "
                                        + "branch: " + studentClass.getBranch().getName()
                                        + ", regulation: " + studentClass.getRegulation().getCode()
                                        + ", academic year: " + studentClass.getAcademicYear().getName()
                        )
                );

        // 3. Get ONLY the subjects for this class's year/semester
        List<CurriculumSubject> curriculumSubjects =
                curriculumSubjectRepository
                        .findByCurriculumAndYearAndSemesterOrderByDisplayOrderAsc(
                                curriculum,
                                studentClass.getYear(),
                                studentClass.getSemester()
                        );

        // 4. Map to DTO and return
        return curriculumSubjects
                .stream()
                .map(curriculumSubjectMapper::toResponse)
                .toList();
    }

    // ------------------------------------------------
    // Duplicate validation
    // ------------------------------------------------

    private void validateDuplicate(
            StudentClass studentClass,
            CurriculumSubject curriculumSubject) {

        if (assignmentRepository
                .existsByStudentClassAndCurriculumSubject(
                        studentClass,
                        curriculumSubject)) {

            throw new ResourceNotFoundException(
                    "Faculty already assigned to this subject."
            );
        }
    }

    /**
     * Validate that the curriculum subject actually belongs to the
     * student class's curriculum (by Branch + Regulation + AcademicYear +
     * Year + Semester).
     *
     * This prevents someone from assigning a subject from a different
     * curriculum to a class (e.g., assigning a CSE subject to a VLSI class).
     */
    private void validateCurriculumSubjectBelongsToClass(
            StudentClass studentClass,
            CurriculumSubject curriculumSubject) {

        Curriculum subjectCurriculum = curriculumSubject.getCurriculum();

        // Check: same branch?
        if (!subjectCurriculum.getBranch().getId()
                .equals(studentClass.getBranch().getId())) {

            throw new BadRequestException(
                    "Subject '" + curriculumSubject.getSubject().getSubjectCode()
                            + "' belongs to branch '"
                            + subjectCurriculum.getBranch().getName()
                            + "', not '" + studentClass.getBranch().getName() + "'"
            );
        }

        // Check: same regulation?
        if (!subjectCurriculum.getRegulation().getId()
                .equals(studentClass.getRegulation().getId())) {

            throw new BadRequestException(
                    "Subject '" + curriculumSubject.getSubject().getSubjectCode()
                            + "' belongs to regulation '"
                            + subjectCurriculum.getRegulation().getCode()
                            + "', not '" + studentClass.getRegulation().getCode() + "'"
            );
        }

        // Check: same academic year?
        if (!subjectCurriculum.getAcademicYear().getId()
                .equals(studentClass.getAcademicYear().getId())) {

            throw new BadRequestException(
                    "Subject '" + curriculumSubject.getSubject().getSubjectCode()
                            + "' belongs to academic year '"
                            + subjectCurriculum.getAcademicYear().getName()
                            + "', not '" + studentClass.getAcademicYear().getName() + "'"
            );
        }

        // Check: same year & semester?
        if (!curriculumSubject.getYear().equals(studentClass.getYear())
                || !curriculumSubject.getSemester().equals(studentClass.getSemester())) {

            throw new BadRequestException(
                    "Subject '" + curriculumSubject.getSubject().getSubjectCode()
                            + "' is taught in year " + curriculumSubject.getYear()
                            + ", semester " + curriculumSubject.getSemester()
                            + ", not year " + studentClass.getYear()
                            + ", semester " + studentClass.getSemester()
            );
        }
    }

    // ------------------------------------------------
    // Assign Faculty
    // ------------------------------------------------

    @Override
    public FacultyAssignmentResponse assignFaculty(
            CreateFacultyAssignmentRequest request) {

        StudentClass studentClass =
                getStudentClass(
                        request.getStudentClassId()
                );

        Faculty faculty =
                getFaculty(
                        request.getFacultyId()
                );

        CurriculumSubject curriculumSubject =
                getCurriculumSubject(
                        request.getCurriculumSubjectId()
                );

        /*
         * VALIDATE: Curriculum subject must belong to this class's
         * curriculum (by Branch + Regulation + AcademicYear + Year +
         * Semester). This prevents assigning a CSE subject to a VLSI
         * class, or a Year 1 subject to a Year 3 class, etc.
         */
        validateCurriculumSubjectBelongsToClass(
                studentClass,
                curriculumSubject
        );

        /*
         * VALIDATE: No duplicate assignment for this subject in this
         * class.
         */
        validateDuplicate(
                studentClass,
                curriculumSubject
        );

        /*
         * NOTE: Faculty branch is NOT validated here.
         *
         * Any faculty can teach any department/branch.
         */

        FacultyAssignment assignment =
                new FacultyAssignment();

        assignment.setStudentClass(studentClass);

        assignment.setFaculty(faculty);

        assignment.setCurriculumSubject(
                curriculumSubject
        );

        assignment =
                assignmentRepository.save(assignment);

        return mapper.map(assignment);
    }

    // ------------------------------------------------
    // Get All Assignments
    // ------------------------------------------------

    @Override
    public List<FacultyAssignmentResponse> getAssignments() {

        return assignmentRepository.findAll()
                .stream()
                .map(mapper::map)
                .toList();
    }

    // ------------------------------------------------
    // Get Assignments By Faculty
    // ------------------------------------------------

    @Override
    public List<FacultyAssignmentResponse> getAssignmentsByFaculty(
            Long facultyId) {

        Faculty faculty = getFaculty(facultyId);

        return assignmentRepository.findByFaculty(faculty)
                .stream()
                .map(mapper::map)
                .toList();
    }

    // ------------------------------------------------
    // Get Assignment By ID
    // ------------------------------------------------

    @Override
    public FacultyAssignmentResponse getAssignment(
            Long id) {

        FacultyAssignment assignment =
                assignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignment Not Found"
                                )
                        );

        return mapper.map(assignment);
    }

    // ------------------------------------------------
    // Delete Assignment
    // ------------------------------------------------

    @Override
    public void deleteAssignment(Long id) {

        FacultyAssignment assignment =
                assignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignment Not Found"
                                )
                        );

        assignmentRepository.delete(assignment);
    }
}