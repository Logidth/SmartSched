package com.smartsched.facultyassignment.mapper;

import com.smartsched.facultyassignment.dto.FacultyAssignmentResponse;
import com.smartsched.facultyassignment.entity.FacultyAssignment;
import org.springframework.stereotype.Component;

@Component
public class FacultyAssignmentMapper {

    public FacultyAssignmentResponse map(FacultyAssignment assignment) {

        return FacultyAssignmentResponse.builder()

                .id(assignment.getId())

                // Student Class
                .studentClassId(
                        assignment.getStudentClass().getId())

                .branch(
                        assignment.getStudentClass()
                                .getBranch()
                                .getName())

                .year(
                        assignment.getStudentClass()
                                .getYear())

                .semester(
                        assignment.getStudentClass()
                                .getSemester())

                .section(
                        assignment.getStudentClass()
                                .getSection())

                // Curriculum Subject
                .curriculumSubjectId(
                        assignment.getCurriculumSubject()
                                .getId())

                .subjectCode(
                        assignment.getCurriculumSubject()
                                .getSubject()
                                .getSubjectCode())

                .subjectName(
                        assignment.getCurriculumSubject()
                                .getSubject()
                                .getSubjectName())

                // Faculty
                .facultyId(
                        assignment.getFaculty()
                                .getId())

                .facultyName(
                        assignment.getFaculty()
                                .getName())

                .employeeId(
                        assignment.getFaculty()
                                .getEmployeeId())

                .build();
    }
}