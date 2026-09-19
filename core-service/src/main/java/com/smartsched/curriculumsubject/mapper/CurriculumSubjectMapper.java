package com.smartsched.curriculumsubject.mapper;

import com.smartsched.common.enums.Status;
import com.smartsched.curriculumsubject.dto.CurriculumSubjectRequest;
import com.smartsched.curriculumsubject.dto.CurriculumSubjectResponse;
import com.smartsched.curriculumsubject.entity.CurriculumSubject;
import org.springframework.stereotype.Component;

@Component
public class CurriculumSubjectMapper {

    public CurriculumSubject toEntity(CurriculumSubjectRequest request) {

        CurriculumSubject curriculumSubject = new CurriculumSubject();

        curriculumSubject.setYear(request.getYear());
        curriculumSubject.setSemester(request.getSemester());
        curriculumSubject.setDisplayOrder(request.getDisplayOrder());
        curriculumSubject.setHoursPerWeek(request.getHoursPerWeek());
        curriculumSubject.setStatus(Status.ACTIVE);

        return curriculumSubject;
    }

    public CurriculumSubjectResponse toResponse(
            CurriculumSubject curriculumSubject) {

        return CurriculumSubjectResponse.builder()
                .id(curriculumSubject.getId())
                .curriculumId(curriculumSubject.getCurriculum().getId())
                .subjectId(curriculumSubject.getSubject().getId())
                .subjectCode(curriculumSubject.getSubject().getSubjectCode())
                .subjectName(curriculumSubject.getSubject().getSubjectName())
                .subjectType(curriculumSubject.getSubject().getSubjectType() != null
                        ? curriculumSubject.getSubject().getSubjectType().name()
                        : null)
                .credits(curriculumSubject.getSubject().getCredits())
                .year(curriculumSubject.getYear())
                .semester(curriculumSubject.getSemester())
                .displayOrder(curriculumSubject.getDisplayOrder())
                .hoursPerWeek(curriculumSubject.getHoursPerWeek())
                .effectiveHoursPerWeek(curriculumSubject.resolveHoursPerWeek())
                .status(curriculumSubject.getStatus().name())
                .build();
    }

    public void updateEntity(
            CurriculumSubject curriculumSubject,
            CurriculumSubjectRequest request) {

        curriculumSubject.setYear(request.getYear());
        curriculumSubject.setSemester(request.getSemester());
        curriculumSubject.setDisplayOrder(request.getDisplayOrder());
        curriculumSubject.setHoursPerWeek(request.getHoursPerWeek());
    }

}