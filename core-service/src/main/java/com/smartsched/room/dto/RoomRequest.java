package com.smartsched.room.dto;

import com.smartsched.common.enums.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequest {

    @NotBlank
    private String roomNumber;

    @NotNull
    private Long blockId;

    @NotNull
    @Min(0)
    private Integer floor;

    @NotNull
    @Min(10)
    private Integer capacity;

    private Long branchId;

    @NotNull
    private Boolean smartRoom;

    @NotNull
    private RoomType roomType;

}