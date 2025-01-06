package com.example.gameserver.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.example.gameserver.api.dto.GetMyTradesRequest;
import com.example.gameserver.api.dto.PlayerTradeRequest;
import com.example.gameserver.api.dto.User.PlayerDetailsDTO;
import com.example.gameserver.entity.*;
import com.example.gameserver.enums.BuildingType;
import com.example.gameserver.exceptions.NoPlayerFoundException;
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
    public Trade playerTrade(PlayerTradeRequest request) {
        if(request == null) {
            logger.error("Request body is null.");
            return null;
        }

        Optional<Game> game = gameRepository.findById(request.getGameId());
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + request.getGameId());
            return null;
        }

        if (request.getOffer() == request.getRequest()) {
            logger.error("Offered and requested resource cannot be the same.");
            return null;
        }

        List<Trade> activeTrades = tradeRepository.findAll().stream().filter(trade -> trade.getFromPlayerId().equals(request.getFromPlayerId()) && trade.getStatus().equals(TradeStatus.ACTIVE)).toList();
        if (!activeTrades.isEmpty()) {
            logger.error("Player " + request.getFromPlayerId() + " already has an active trade listed.");
            return null;
        }

        Optional<Resources> resources = resourceRepository.findByGameIdAndPlayerId(request.getGameId(), request.getFromPlayerId());
        if (resources.isEmpty()) {
            logger.error("Resources not found, Game ID: " + request.getGameId() + ", Player ID: " + request.getFromPlayerId());
            return null;
        }

        Resources fromPlayerResources = resources.get();
        if(fromPlayerResources.hasEnoughResources(request.getOffer(), 1)) {
            Set<User> players = gameRepository.findById(request.getGameId()).get().getPlayers();
            if(players.isEmpty()) {
                throw new NoPlayerFoundException("GameId: " + request.getGameId());
            }
            for(User player : players) {
                if(player.getId().equals(request.getFromPlayerId()))
                    continue;
                Optional<Resources> toResources = resourceRepository.findByGameIdAndPlayerId(request.getGameId(), player.getId());
                if (toResources.isEmpty()) {
                    logger.error("Resources not found, Game ID: " + request.getGameId() + ", Player ID: " + player.getId());
                    return null;
                }
                Resources toPlayerResources = resources.get();
                if(toPlayerResources.hasEnoughResources(request.getRequest(), 1)) {
                    Trade trade = new Trade(request.getGameId(), request.getFromPlayerId(), player.getId(), request.getOffer(), request.getRequest(), TradeStatus.ACTIVE);
                    tradeRepository.save(trade);
                    return trade;
                }
                logger.error("There are no players having the requested resource.");
                return null;
            }
        }
        else {
            logger.error("Player does not have the offering resources.");
        }
        return null;
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

    @Transactional
    public List<Trade> getMyActiveTrades(Long gameId, Long playerId) {
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + gameId);
            return null;
        }

        List<Trade> activeTrades = tradeRepository.findAll().stream().filter(trade -> trade.getToPlayerId().equals(playerId) && trade.getStatus().equals(TradeStatus.ACTIVE)).toList();
        if (activeTrades.isEmpty()) {
            logger.error("No active trades are available for player " + playerId + " to deal with.");
            return null;
        }
        return activeTrades;
    }

    @Transactional
    public TradeStatus acceptTrade(Long gameId, Long playerId, Long tradeId) {
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + gameId);
            return TradeStatus.CANCELLED;
        }

        Optional<Trade> optionalTrade = tradeRepository.findByGameIdAndTradeId(gameId, tradeId);
        if (optionalTrade.isEmpty()) {
            logger.error("Trade not found, ID: " + tradeId);
            return TradeStatus.CANCELLED;
        }
        Trade trade = optionalTrade.get();

        Optional<Resources> fromResources = resourceRepository.findByGameIdAndPlayerId(gameId, trade.getFromPlayerId());
        if (fromResources.isEmpty()) {
            logger.error("Resources not found, Game ID: " + gameId + ", Player ID: " + playerId);
            return TradeStatus.CANCELLED;
        }

        Optional<Resources> toResources = resourceRepository.findByGameIdAndPlayerId(gameId, playerId);
        if (toResources.isEmpty()) {
            logger.error("Resources not found, Game ID: " + gameId + ", Player ID: " + playerId);
            return TradeStatus.CANCELLED;
        }

        Resources fromPlayerResources = fromResources.get();
        Resources toPlayerResources = toResources.get();
        if(fromPlayerResources.hasEnoughResources(trade.getOffering(), 1) && toPlayerResources.hasEnoughResources(trade.getRequesting(), 1)) {
            fromPlayerResources.subtract(trade.getOffering(), 1);
            fromPlayerResources.add(trade.getRequesting(), 1);
            toPlayerResources.subtract(trade.getRequesting(), 1);
            toPlayerResources.add(trade.getOffering(), 1);
            resourceRepository.save(fromPlayerResources);
            resourceRepository.save(toPlayerResources);
            return TradeStatus.COMPLETED;
        }
        else {
            logger.error("Players have no longer enough resources to trade.");
        }
        return TradeStatus.CANCELLED;
    }


    // Still to be discussed
    @Transactional
    public Trade declineTrade(String gameId, String playerId, String tradeId) {
        return null;
    }

    



    
}
