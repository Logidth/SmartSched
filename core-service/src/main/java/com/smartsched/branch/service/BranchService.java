package com.smartsched.branch.service;

import com.smartsched.branch.dto.BranchRequest;
import com.smartsched.branch.dto.BranchResponse;

import java.util.List;

public interface BranchService {

    BranchResponse createBranch(BranchRequest request);

    BranchResponse updateBranch(Long id, BranchRequest request);

    BranchResponse getBranch(Long id);

    List<BranchResponse> getAllBranches();

    void deleteBranch(Long id);

    BranchResponse activateBranch(Long id);

    BranchResponse deactivateBranch(Long id);
}