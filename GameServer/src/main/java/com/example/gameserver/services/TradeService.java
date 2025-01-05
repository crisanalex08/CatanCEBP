package com.example.gameserver.services;

import java.util.List;
import java.util.Optional;

import com.example.gameserver.api.dto.User.PlayerDetailsDTO;
import com.example.gameserver.entity.Building;
import com.example.gameserver.entity.Game;
import com.example.gameserver.entity.Resources;
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
    public TradeStatus merchantTrade(TradeCreateRequestDTO request) {
        if(request == null) {
            logger.error("Request body is null.");
            return TradeStatus.CANCELLED;
        }

        Optional<Game> game = gameRepository.findById(request.getGameId());
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + request.getGameId());
            return TradeStatus.CANCELLED;
        }

        if (request.getOffer() == request.getRequest()) {
            logger.error("Offered and requested resource cannot be the same.");
            return TradeStatus.CANCELLED;
        }

        Optional<Resources> resources = resourceRepository.findByGameIdAndPlayerId(request.getGameId(), request.getPlayerId());
        if (resources.isEmpty()) {
            logger.error("Resources not found, Game ID: " + request.getGameId() + ", Player ID: " + request.getPlayerId());
            return TradeStatus.CANCELLED;
        }
        Resources playerResources = resources.get();
        if(playerResources.hasEnoughResources(request.getOffer(), 3)) {
            playerResources.subtract(request.getOffer(), 3);
            playerResources.add(request.getRequest(), 1);
            resourceRepository.save(playerResources);
            return TradeStatus.COMPLETED;
        }
        else {
            logger.error("Player has not enough offering resources.");
        }
        return TradeStatus.CANCELLED;
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
