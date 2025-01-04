package com.example.gameserver.services;

import java.util.List;
import java.util.Optional;

import com.example.gameserver.api.dto.User.PlayerDetailsDTO;
import com.example.gameserver.entity.Building;
import com.example.gameserver.entity.Game;
import com.example.gameserver.repository.*;
import com.example.gameserver.enums.TradeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.gameserver.repository.GameRepository;
import com.example.gameserver.repository.ResourceRepository;
import com.example.gameserver.repository.TradeRepository;

import com.example.gameserver.api.dto.TradeCreateRequestDTO;
import com.example.gameserver.entity.Trade;

import jakarta.transaction.Transactional;


@Service
public class TradeService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);
    private final GameRepository gameRepository;
    private final ResourceRepository resourceRepository;

    private final TradeRepository tradeRepository;
    // TO DO: 
    // 1) A single trade should be active at a time
    // 2) Multiple trades should be active at a time and should be able to be accepted or declined by other players


    public TradeService(GameRepository gameRepository, ResourceRepository resourceRepository, TradeRepository tradeRepository) {
        this.gameRepository = gameRepository;
        this.resourceRepository = resourceRepository;
        this.tradeRepository = tradeRepository;
    }
    @Transactional
    public Trade createTrade(TradeCreateRequestDTO request) {
//        if(request == null) {
//            return null;
//        }
//
//        Optional<Game> game = gameRepository.findById(request.getGameId());
//        if (game.isEmpty()) {
//            logger.error("Game not found, ID: " + request.getGameId());
//            return null;
//        }
//
//        PlayerDetailsDTO fromPlayer = game.get().getPlayerById(request.getFromPlayerId());
//        if (fromPlayer == null) {
//            logger.error("Player not found, ID: " + (request.getFromPlayerId()));
//            return null;
//        }
//
//        PlayerDetailsDTO toPlayer = game.get().getPlayerById(request.getToPlayerId());
//        if (toPlayer == null) {
//            logger.error("Player not found, ID: " + (request.getToPlayerId()));
//            return null;
//        }

        //check if toPlayerId has the resources available

//        Trade trade = new Trade(request.getGameId(), request.getFromPlayerId(), request.getToPlayerId(), request.getOffer(), request.getRequest(), TradeStatus.ACTIVE);
        Trade trade = new Trade(request.getGameId(), request.getFromPlayerId(), request.getToPlayerId(), /*request.getOffer(), request.getRequest(),*/ TradeStatus.ACTIVE);
        tradeRepository.save(trade);
//        System.out.println(request.getGameId());
        return trade;

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
