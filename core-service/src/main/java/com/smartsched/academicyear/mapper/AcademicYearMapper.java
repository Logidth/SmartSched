package com.smartsched.academicyear.mapper;

import com.smartsched.academicyear.dto.AcademicYearRequest;
import com.smartsched.academicyear.dto.AcademicYearResponse;
import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.common.enums.Status;
import org.springframework.stereotype.Component;

@Component
public class AcademicYearMapper {

    public AcademicYear toEntity(AcademicYearRequest request) {

        AcademicYear academicYear = new AcademicYear();

        academicYear.setName(request.getName().trim());
        academicYear.setStatus(Status.ACTIVE);

        return academicYear;
    }

    public AcademicYearResponse toResponse(AcademicYear academicYear) {

        return AcademicYearResponse.builder()
                .id(academicYear.getId())
                .name(academicYear.getName())
                .status(academicYear.getStatus().name())
                .build();
    }

    public void updateEntity(
            AcademicYear academicYear,
            AcademicYearRequest request) {

        academicYear.setName(request.getName().trim());
    }
}