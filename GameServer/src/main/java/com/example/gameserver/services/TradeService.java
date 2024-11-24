package com.example.gameserver.services;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.gameserver.api.dto.TradeCreateRequestDTO;
import com.example.gameserver.entity.Trade;

import jakarta.transaction.Transactional;


@Service
public class TradeService {
    // TO DO: 
    // 1) A single trade should be active at a time
    // 2) Multiple trades should be active at a time and should be able to be accepted or declined by other players

    @Transactional
    public Trade createTrade(String gameId, String playerId, TradeCreateRequestDTO request) {
        return null;
    }

    @Async
    public Trade getTrade(String gameId, String tradeId) {
        return null;
    }
    
    @Async
    public List<Trade> getTrades(String gameId) {
        return null;
    }

    @Transactional
    public Trade cancelTrade(String gameId, String playerId, String tradeId) {
        return null;
    }

    @Transactional
    public Trade acceptTrade(String gameId, String playerId, String tradeId) {
        return null;
    }
    // Still to be discussed
    @Transactional
    public Trade declineTrade(String gameId, String playerId, String tradeId) {
        return null;
    }

    



    
}
