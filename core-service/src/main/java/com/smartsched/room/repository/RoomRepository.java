package com.smartsched.room.repository;

import com.smartsched.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    List<Room> findByBranchId(Long branchId);

    List<Room> findByRoomType(com.smartsched.common.enums.RoomType roomType);

    long count();

}