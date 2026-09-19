package com.smartsched.room.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponse {

    private Long id;

    private String roomNumber;

    private Long blockId;

    private String blockName;

    private Integer floor;

    private Integer capacity;

    private Boolean smartRoom;

    private String roomType;

    private String status;

    private Long branchId;

    private String branchName;

}