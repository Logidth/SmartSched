package com.smartsched.substitute.mapper;

import com.smartsched.substitute.dto.SubstituteResponse;
import com.smartsched.substitute.entity.SubstituteAssignment;
import org.springframework.stereotype.Component;

@Component
public class SubstituteMapper {

    public SubstituteResponse map(SubstituteAssignment assignment) {

        return SubstituteResponse.builder()

                .id(assignment.getId())

                .originalFaculty(
                        assignment.getOriginalFaculty().getName()
                )

                .substituteFaculty(
                        assignment.getSubstituteFaculty().getName()
                )

                .subject(
                        assignment.getLectureLog()
                                .getTimetableEntry()
                                .getSubject()
                                .getSubjectName()
                )

                .className(
                        assignment.getLectureLog()
                                .getTimetableEntry()
                                .getStudentClass()
                                .getBranch()
                                .getName()
                                + " "
                                + assignment.getLectureLog()
                                .getTimetableEntry()
                                .getStudentClass()
                                .getYear()
                                + "-"
                                + assignment.getLectureLog()
                                .getTimetableEntry()
                                .getStudentClass()
                                .getSection()
                )

                .assignmentDate(
                        assignment.getAssignmentDate()
                )

                .assignedAt(
                        assignment.getAssignedAt()
                )

                .accepted(
                        assignment.getAccepted()
                )

                .reason(
                        assignment.getReason()
                )

                .build();
    }
}