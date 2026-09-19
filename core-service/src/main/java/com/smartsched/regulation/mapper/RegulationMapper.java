package com.smartsched.regulation.mapper;

import com.smartsched.common.enums.Status;
import com.smartsched.regulation.dto.RegulationRequest;
import com.smartsched.regulation.dto.RegulationResponse;
import com.smartsched.regulation.entity.Regulation;
import org.springframework.stereotype.Component;

@Component
public class RegulationMapper {

    public Regulation toEntity(RegulationRequest request) {

        Regulation regulation = new Regulation();

        regulation.setCode(request.getCode().trim().toUpperCase());
        regulation.setDescription(request.getDescription().trim());
        regulation.setStatus(Status.ACTIVE);

        return regulation;
    }

    public RegulationResponse toResponse(Regulation regulation) {

        return RegulationResponse.builder()
                .id(regulation.getId())
                .code(regulation.getCode())
                .description(regulation.getDescription())
                .status(regulation.getStatus().name())
                .build();
    }

    public void updateEntity(Regulation regulation,
                             RegulationRequest request) {

        regulation.setCode(request.getCode().trim().toUpperCase());
        regulation.setDescription(request.getDescription().trim());
    }

}