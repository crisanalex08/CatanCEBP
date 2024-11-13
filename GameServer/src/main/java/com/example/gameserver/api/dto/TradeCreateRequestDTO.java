package com.example.gameserver.api.dto;

import com.example.gameserver.entity.Resources;

import lombok.Data;


@Data


public class TradeCreateRequestDTO {
    
    private String gameId;
    private String playerId;
    private Resources offer;
    private Resources request;
}
