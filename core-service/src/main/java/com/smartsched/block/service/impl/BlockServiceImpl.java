package com.smartsched.block.service.impl;

import com.smartsched.block.dto.BlockRequest;
import com.smartsched.block.dto.BlockResponse;
import com.smartsched.block.entity.Block;
import com.smartsched.block.mapper.BlockMapper;
import com.smartsched.block.repository.BlockRepository;
import com.smartsched.block.service.BlockService;
import com.smartsched.common.enums.Status;
import com.smartsched.common.exception.ConflictException;
import com.smartsched.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;
    private final BlockMapper mapper;

    @Override
    public BlockResponse create(BlockRequest request) {

        blockRepository.findByName(request.getName().trim())
                .ifPresent(b -> {
                    throw new ConflictException("Block already exists.");
                });

        Block saved = blockRepository.save(mapper.toEntity(request));

        return mapper.toResponse(saved);
    }

    @Override
    public BlockResponse update(Long id, BlockRequest request) {

        Block block = blockRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Block not found."));

        blockRepository.findByName(request.getName().trim())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {
                        throw new ConflictException("Block already exists.");
                    }

                });

        mapper.updateEntity(block, request);

        return mapper.toResponse(blockRepository.save(block));
    }

    @Override
    public BlockResponse getById(Long id) {

        return mapper.toResponse(
                blockRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Block not found."))
        );
    }

    @Override
    public List<BlockResponse> getAll() {

        return blockRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public BlockResponse activate(Long id) {

        Block block = blockRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Block not found."));

        block.setStatus(Status.ACTIVE);

        return mapper.toResponse(blockRepository.save(block));
    }

    @Override
    public BlockResponse deactivate(Long id) {

        Block block = blockRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Block not found."));

        block.setStatus(Status.INACTIVE);

        return mapper.toResponse(blockRepository.save(block));
    }

    @Override
    public void delete(Long id) {

        Block block = blockRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Block not found."));

        block.setStatus(Status.INACTIVE);

        blockRepository.save(block);
    }
}