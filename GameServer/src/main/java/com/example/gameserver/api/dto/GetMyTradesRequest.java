package com.example.gameserver.api.dto;

import com.example.gameserver.entity.Resources;

import com.example.gameserver.enums.ResourceType;
import lombok.Data;


@Data
public class GetMyTradesRequest {
    private Long gameId;
    private Long PlayerId;
}
