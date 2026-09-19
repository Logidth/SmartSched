package com.smartsched.regulation.service.impl;

import com.smartsched.common.enums.Status;
import com.smartsched.common.exception.ConflictException;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.regulation.dto.RegulationRequest;
import com.smartsched.regulation.dto.RegulationResponse;
import com.smartsched.regulation.entity.Regulation;
import com.smartsched.regulation.mapper.RegulationMapper;
import com.smartsched.regulation.repository.RegulationRepository;
import com.smartsched.regulation.service.RegulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegulationServiceImpl implements RegulationService {

    private final RegulationRepository regulationRepository;
    private final RegulationMapper mapper;

    @Override
    public RegulationResponse create(RegulationRequest request) {

        regulationRepository.findByCodeIgnoreCase(request.getCode().trim())
                .ifPresent(regulation -> {
                    throw new ConflictException("Regulation already exists.");
                });

        Regulation regulation = mapper.toEntity(request);

        return mapper.toResponse(regulationRepository.save(regulation));
    }

    @Override
    public RegulationResponse update(Long id,
                                     RegulationRequest request) {

        Regulation regulation = regulationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Regulation not found."));

        regulationRepository.findByCodeIgnoreCase(request.getCode().trim())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {
                        throw new ConflictException("Regulation already exists.");
                    }

                });

        mapper.updateEntity(regulation, request);

        return mapper.toResponse(regulationRepository.save(regulation));
    }

    @Override
    public RegulationResponse getById(Long id) {

        Regulation regulation = regulationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Regulation not found."));

        return mapper.toResponse(regulation);
    }

    @Override
    public List<RegulationResponse> getAll() {

        return regulationRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public RegulationResponse activate(Long id) {

        Regulation regulation = regulationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Regulation not found."));

        regulation.setStatus(Status.ACTIVE);

        return mapper.toResponse(regulationRepository.save(regulation));
    }

    @Override
    public RegulationResponse deactivate(Long id) {

        Regulation regulation = regulationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Regulation not found."));

        regulation.setStatus(Status.INACTIVE);

        return mapper.toResponse(regulationRepository.save(regulation));
    }

    @Override
    public void delete(Long id) {

        Regulation regulation = regulationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Regulation not found."));

        regulation.setStatus(Status.INACTIVE);

        regulationRepository.save(regulation);
    }
}