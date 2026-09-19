package com.smartsched.branch.service.impl;

import com.smartsched.branch.dto.BranchRequest;
import com.smartsched.branch.dto.BranchResponse;
import com.smartsched.branch.entity.Branch;
import com.smartsched.branch.mapper.BranchMapper;
import com.smartsched.branch.repository.BranchRepository;
import com.smartsched.branch.service.BranchService;
import com.smartsched.common.exception.ConflictException;
import com.smartsched.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    @Override
    public BranchResponse createBranch(BranchRequest request) {

        if (branchRepository.existsByCodeIgnoreCase((request.getCode().trim()))){
            throw new ConflictException("Branch code already exists.");
        }

        if (branchRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ConflictException("Branch name already exists.");
        }

        Branch branch = branchMapper.toEntity(request);

        Branch savedBranch = branchRepository.save(branch);

        return branchMapper.toResponse(savedBranch);
    }

    @Override
    public BranchResponse updateBranch(Long id, BranchRequest request) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found."));

        branchRepository.findByCodeIgnoreCase(request.getCode().trim())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ConflictException("Branch code already exists.");
                    }
                });

        branchRepository.findByNameIgnoreCase(request.getName().trim())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ConflictException("Branch name already exists.");
                    }
                });

        branchMapper.updateEntity(branch, request);

        Branch updated = branchRepository.save(branch);

        return branchMapper.toResponse(updated);
    }

    @Override
    public BranchResponse getBranch(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found."));

        return branchMapper.toResponse(branch);
    }

    @Override
    public List<BranchResponse> getAllBranches() {

        return branchRepository.findAll()
                .stream()
                .map(branchMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteBranch(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found."));

        branch.setActive(false);

        branchRepository.save(branch);
    }

    @Override
    public BranchResponse activateBranch(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found."));

        branch.setActive(true);

        return branchMapper.toResponse(branchRepository.save(branch));
    }

    @Override
    public BranchResponse deactivateBranch(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found."));

        branch.setActive(false);

        return branchMapper.toResponse(branchRepository.save(branch));
    }
}