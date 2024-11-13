package com.example.gameserver.services;

import org.springframework.stereotype.Service;


import com.example.gameserver.aggregates.Trade;
import com.example.gameserver.aggregates.TradeCreateRequest;


@Service
public class TradeService {


    public Trade createTrade(String gameId, String playerId, TradeCreateRequest request) {
        return null;
    }

    public Trade getTrade(String gameId, String tradeId) {
        return null;
    }
    public Trade getTrades(String gameId) {
        return null;
    }

    public Trade cancelTrade(String gameId, String playerId, String tradeId) {
        return null;
    }

    public Trade acceptTrade(String gameId, String playerId, String tradeId) {
        return null;
    }

    public Trade declineTrade(String gameId, String playerId, String tradeId) {
        return null;
    }

    



    
}
