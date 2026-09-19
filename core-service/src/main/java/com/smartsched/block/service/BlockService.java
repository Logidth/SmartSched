package com.smartsched.block.service;

import com.smartsched.block.dto.BlockRequest;
import com.smartsched.block.dto.BlockResponse;

import java.util.List;

public interface BlockService {

    BlockResponse create(BlockRequest request);

    BlockResponse update(Long id, BlockRequest request);

    BlockResponse getById(Long id);

    List<BlockResponse> getAll();

    BlockResponse activate(Long id);

    BlockResponse deactivate(Long id);

    void delete(Long id);

}