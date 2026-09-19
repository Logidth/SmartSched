package com.smartsched.regulation.service;

import com.smartsched.regulation.dto.RegulationRequest;
import com.smartsched.regulation.dto.RegulationResponse;

import java.util.List;

public interface RegulationService {

    RegulationResponse create(RegulationRequest request);

    RegulationResponse update(Long id, RegulationRequest request);

    RegulationResponse getById(Long id);

    List<RegulationResponse> getAll();

    RegulationResponse activate(Long id);

    RegulationResponse deactivate(Long id);

    void delete(Long id);

}