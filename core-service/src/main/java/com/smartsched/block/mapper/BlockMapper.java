package com.smartsched.block.mapper;

import com.smartsched.block.dto.BlockRequest;
import com.smartsched.block.dto.BlockResponse;
import com.smartsched.block.entity.Block;
import com.smartsched.common.enums.Status;
import org.springframework.stereotype.Component;

@Component
public class BlockMapper {

    public Block toEntity(BlockRequest request){

        Block block = new Block();

        block.setName(request.getName().trim());
        block.setDescription(request.getDescription());
        block.setStatus(Status.ACTIVE);

        return block;
    }

    public BlockResponse toResponse(Block block){

        return BlockResponse.builder()
                .id(block.getId())
                .name(block.getName())
                .description(block.getDescription())
                .status(block.getStatus().name())
                .build();
    }

    public void updateEntity(Block block, BlockRequest request){

        block.setName(request.getName().trim());
        block.setDescription(request.getDescription());

    }

}