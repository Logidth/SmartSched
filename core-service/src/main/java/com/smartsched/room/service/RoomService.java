package com.smartsched.room.service;

import com.smartsched.room.dto.RoomRequest;
import com.smartsched.room.dto.RoomResponse;

import java.util.List;

public interface RoomService {

    RoomResponse create(RoomRequest request);

    RoomResponse update(Long id, RoomRequest request);

    RoomResponse getById(Long id);

    List<RoomResponse> getAll();

    List<RoomResponse> getByBranch(Long branchId);

    List<RoomResponse> getByRoomType(String roomType);

    RoomResponse activate(Long id);

    RoomResponse deactivate(Long id);

    void delete(Long id);

}