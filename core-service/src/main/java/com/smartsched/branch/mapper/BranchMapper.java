package com.smartsched.branch.mapper;

import com.smartsched.branch.dto.BranchRequest;
import com.smartsched.branch.dto.BranchResponse;
import com.smartsched.branch.entity.Branch;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {

    public Branch toEntity(BranchRequest request) {

        Branch branch = new Branch();

        branch.setName(request.getName().trim());
        branch.setCode(request.getCode().trim().toUpperCase());
        branch.setDescription(request.getDescription());

        return branch;
    }

    public BranchResponse toResponse(Branch branch) {

        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .code(branch.getCode())
                .description(branch.getDescription())
                .active(branch.isActive())
                .build();
    }

    public void updateEntity(Branch branch, BranchRequest request) {

        branch.setName(request.getName().trim());
        branch.setCode(request.getCode().trim().toUpperCase());
        branch.setDescription(request.getDescription());
    }
}