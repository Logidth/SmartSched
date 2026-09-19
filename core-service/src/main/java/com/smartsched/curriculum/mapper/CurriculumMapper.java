package com.smartsched.curriculum.mapper;

import com.smartsched.common.enums.Status;
import com.smartsched.curriculum.dto.CurriculumRequest;
import com.smartsched.curriculum.dto.CurriculumResponse;
import com.smartsched.curriculum.entity.Curriculum;
import org.springframework.stereotype.Component;

@Component
public class CurriculumMapper {

    public Curriculum toEntity(CurriculumRequest request) {

        Curriculum curriculum = new Curriculum();

        curriculum.setStatus(Status.ACTIVE);

        return curriculum;
    }

    public CurriculumResponse toResponse(Curriculum curriculum) {

        return CurriculumResponse.builder()
                .id(curriculum.getId())
                .branchId(curriculum.getBranch().getId())
                .branch(curriculum.getBranch().getName())
                .regulationId(curriculum.getRegulation().getId())
                .regulation(curriculum.getRegulation().getCode())
                .academicYearId(curriculum.getAcademicYear().getId())
                .academicYear(curriculum.getAcademicYear().getName())
                .status(curriculum.getStatus().name())
                .build();
    }

    public void updateEntity(
            Curriculum curriculum,
            CurriculumRequest request) {

        // Relationships are updated in the service layer.
    }
}