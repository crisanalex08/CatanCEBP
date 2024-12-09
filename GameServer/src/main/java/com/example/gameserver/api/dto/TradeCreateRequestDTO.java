package com.example.gameserver.api.dto;

import com.example.gameserver.entity.Resources;

import lombok.Data;


@Data
public class TradeCreateRequestDTO {
    private Long gameId;
    private Long fromPlayerId;
    private Long toPlayerId; //to be implemented for this to be chosen by the game
    private Resources offer;
    private Resources request;
}
