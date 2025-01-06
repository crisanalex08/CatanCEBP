package com.example.gameserver.api.dto;

import com.example.gameserver.entity.Resources;

import com.example.gameserver.enums.ResourceType;
import lombok.Data;


@Data
public class PlayerTradeRequest {
    private Long gameId;
    private Long fromPlayerId;
//    private Long toPlayerId; //to be implemented for this to be chosen by the game
    private ResourceType offer;
    private ResourceType request;
}
