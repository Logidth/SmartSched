package com.smartsched.room.mapper;

import com.smartsched.common.enums.Status;
import com.smartsched.room.dto.RoomRequest;
import com.smartsched.room.dto.RoomResponse;
import com.smartsched.room.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toEntity(RoomRequest request) {

        Room room = new Room();

        room.setRoomNumber(request.getRoomNumber().trim().toUpperCase());

        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());
        room.setSmartRoom(request.getSmartRoom());
        room.setRoomType(request.getRoomType());
        room.setStatus(Status.ACTIVE);

        return room;
    }

    public RoomResponse toResponse(Room room) {

        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .blockId(room.getBlock().getId())
                .blockName(room.getBlock().getName())
                .floor(room.getFloor())
                .capacity(room.getCapacity())
                .smartRoom(room.getSmartRoom())
                .roomType(room.getRoomType().name())
                .status(room.getStatus().name())
                .branchId(room.getBranch() != null ? room.getBranch().getId() : null)
                .branchName(room.getBranch() != null ? room.getBranch().getName() : null)
                .build();
    }

    public void updateEntity(Room room, RoomRequest request) {

        room.setRoomNumber(request.getRoomNumber().trim().toUpperCase());

        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());
        room.setSmartRoom(request.getSmartRoom());
        room.setRoomType(request.getRoomType());

    }

}