package com.example.gameserver.api.dto;

import com.example.gameserver.enums.ResourceType;
import lombok.Data;


@Data
public class TradeRequest {
    private Long gameId;
    private Long playerId;
    private ResourceType offer;
    private ResourceType request;

    public TradeRequest(Long gameId, Long playerId, ResourceType offer, ResourceType request) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.offer = offer;
        this.request = request;
    }
}
