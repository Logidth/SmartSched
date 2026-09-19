package com.smartsched.block.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlockResponse {

    private Long id;

    private String name;

    private String description;

    private String status;

}