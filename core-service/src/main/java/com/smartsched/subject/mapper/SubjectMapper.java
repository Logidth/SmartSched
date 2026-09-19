package com.smartsched.subject.mapper;

import com.smartsched.common.enums.Status;
import com.smartsched.subject.dto.SubjectRequest;
import com.smartsched.subject.dto.SubjectResponse;
import com.smartsched.subject.entity.Subject;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {

    public Subject toEntity(SubjectRequest request) {

        Subject subject = new Subject();

        subject.setSubjectCode(request.getSubjectCode().trim().toUpperCase());
        subject.setSubjectName(request.getSubjectName().trim());

        subject.setCredits(request.getCredits());

        subject.setTheoryHours(request.getTheoryHours());
        subject.setLabHours(request.getLabHours());

        // Backend calculates total hours
        subject.setTotalHours(
                request.getTheoryHours() + request.getLabHours()
        );

        subject.setHoursPerWeek(request.getHoursPerWeek());

        subject.setSubjectType(request.getSubjectType());

        subject.setRequiresLabRoom(request.getRequiresLabRoom());

        subject.setStatus(Status.ACTIVE);
        subject.setSubjectCategory(
                request.getSubjectCategory()
        );

        return subject;
    }

    public SubjectResponse toResponse(Subject subject) {

        return SubjectResponse.builder()
                .id(subject.getId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .regulationId(subject.getRegulation().getId())
                .regulation(subject.getRegulation().getCode())
                .credits(subject.getCredits())
                .theoryHours(subject.getTheoryHours())
                .labHours(subject.getLabHours())
                .totalHours(subject.getTotalHours())
                .hoursPerWeek(subject.getHoursPerWeek())
                .subjectType(subject.getSubjectType().name())
                .requiresLabRoom(subject.getRequiresLabRoom())
                .status(subject.getStatus().name())
                .subjectCategory(
                        subject.getSubjectCategory().name()
                )
                .build();
    }

    public void updateEntity(
            Subject subject,
            SubjectRequest request
    ) {

        subject.setSubjectCode(request.getSubjectCode().trim().toUpperCase());

        subject.setSubjectName(request.getSubjectName().trim());

        subject.setCredits(request.getCredits());

        subject.setTheoryHours(request.getTheoryHours());

        subject.setLabHours(request.getLabHours());

        subject.setHoursPerWeek(request.getHoursPerWeek());

        subject.setSubjectType(request.getSubjectType());

        subject.setRequiresLabRoom(request.getRequiresLabRoom());
        subject.setSubjectCategory(
                request.getSubjectCategory()
        );

    }
}