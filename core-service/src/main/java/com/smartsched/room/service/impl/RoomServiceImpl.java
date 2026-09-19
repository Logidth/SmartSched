package com.smartsched.room.service.impl;

import com.smartsched.block.entity.Block;
import com.smartsched.block.repository.BlockRepository;
import com.smartsched.branch.entity.Branch;
import com.smartsched.branch.repository.BranchRepository;
import com.smartsched.common.enums.RoomType;
import com.smartsched.common.enums.Status;
import com.smartsched.common.exception.ConflictException;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.room.dto.RoomRequest;
import com.smartsched.room.dto.RoomResponse;
import com.smartsched.room.entity.Room;
import com.smartsched.room.mapper.RoomMapper;
import com.smartsched.room.repository.RoomRepository;
import com.smartsched.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final BlockRepository blockRepository;
    private final RoomRepository roomRepository;
    private final BranchRepository branchRepository;
    private final RoomMapper mapper;

    @Override
    public RoomResponse create(RoomRequest request) {

        roomRepository.findByRoomNumber(
                        request.getRoomNumber().trim().toUpperCase())
                .ifPresent(room -> {
                    throw new ConflictException("Room already exists.");
                });

        Room room = mapper.toEntity(request);
        Block block = blockRepository.findById(request.getBlockId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Block not found."));

        room.setBlock(block);

        if (request.getBranchId() != null) {

            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Branch not found."));

            room.setBranch(branch);
        }

        Room saved = roomRepository.save(room);

        return mapper.toResponse(saved);
    }

    @Override
    public RoomResponse update(Long id, RoomRequest request) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found."));
        Block block = blockRepository.findById(request.getBlockId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Block not found."));

        room.setBlock(block);

        roomRepository.findByRoomNumber(
                        request.getRoomNumber().trim().toUpperCase())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {
                        throw new ConflictException("Room already exists.");
                    }

                });

        mapper.updateEntity(room, request);

        if (request.getBranchId() != null) {

            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Branch not found."));

            room.setBranch(branch);

        } else {

            room.setBranch(null);

        }

        Room updated = roomRepository.save(room);

        return mapper.toResponse(updated);
    }

    @Override
    public RoomResponse getById(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found."));

        return mapper.toResponse(room);
    }

    @Override
    public List<RoomResponse> getAll() {

        return roomRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<RoomResponse> getByBranch(Long branchId) {

        return roomRepository.findByBranchId(branchId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<RoomResponse> getByRoomType(String roomType) {

        RoomType type;

        try {

            type = RoomType.valueOf(roomType.toUpperCase());

        } catch (Exception e) {

            throw new IllegalArgumentException("Invalid room type.");

        }

        return roomRepository.findByRoomType(type)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public RoomResponse activate(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found."));

        room.setStatus(Status.ACTIVE);

        return mapper.toResponse(roomRepository.save(room));
    }

    @Override
    public RoomResponse deactivate(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found."));

        room.setStatus(Status.INACTIVE);

        return mapper.toResponse(roomRepository.save(room));
    }

    @Override
    public void delete(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found."));

        room.setStatus(Status.INACTIVE);

        roomRepository.save(room);
    }
}