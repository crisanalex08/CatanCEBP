package com.example.gameserver.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.gameserver.api.dto.TradeCreateRequestDTO;
import com.example.gameserver.entity.Trade;


@Service
public class TradeService {


    public Trade createTrade(String gameId, String playerId, TradeCreateRequestDTO request) {
        return null;
    }

    public Trade getTrade(String gameId, String tradeId) {
        return null;
    }
    public List<Trade> getTrades(String gameId) {
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
