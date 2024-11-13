package com.example.gameserver.aggregates;

import lombok.Data;

@Data
public class TradeCreateRequest {
    private String gameId;
    private Resources offer;
    private Resources request;
    
}
