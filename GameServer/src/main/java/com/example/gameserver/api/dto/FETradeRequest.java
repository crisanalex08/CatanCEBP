package com.example.gameserver.api.dto;

import com.example.gameserver.enums.ResourceType;
import lombok.Data;


@Data
public class FETradeRequest {
    private Long gameId;
    private Long playerId;
    private String offer;
    private String request;
}
