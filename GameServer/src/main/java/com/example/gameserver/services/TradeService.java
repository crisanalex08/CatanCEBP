package com.example.gameserver.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.example.gameserver.entity.*;
import com.example.gameserver.exceptions.GameNotFoundException;
import com.example.gameserver.exceptions.NoPlayerFoundException;
import com.example.gameserver.enums.TradeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.gameserver.repository.GameRepository;
import com.example.gameserver.repository.ResourceRepository;
import com.example.gameserver.repository.TradeRepository;

import com.example.gameserver.api.dto.TradeRequest;

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
    public Trade playerTrade(TradeRequest request) {
        if(request == null) {
            logger.error("Request body is null.");
            return null;
        }

        Optional<Game> game = gameRepository.findById(request.getGameId());
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + request.getGameId());
            throw new GameNotFoundException(request.getGameId());
        }

        if (request.getOffer() == request.getRequest()) {
            logger.error("Offered and requested resource cannot be the same.");
           throw new IllegalArgumentException("Offered and requested resource cannot be the same.");
        }

        List<Trade> activeTrades = tradeRepository.findAll().stream().filter(trade -> trade.getFromPlayerId().equals(request.getPlayerId()) && trade.getStatus().equals(TradeStatus.ACTIVE)).toList();
        if (!activeTrades.isEmpty()) {
            logger.error("Player " + request.getPlayerId() + " already has an active trade listed.");
            throw new IllegalArgumentException("Player already has an active trade listed.");
        }

        Optional<Resources> resources = resourceRepository.findByGameIdAndPlayerId(request.getGameId(), request.getPlayerId());
        if (resources.isEmpty()) {
            logger.error("Resources not found, Game ID: " + request.getGameId() + ", Player ID: " + request.getPlayerId());
          throw new IllegalArgumentException("Resources not found, Game ID: " + request.getGameId() + ", Player ID: " + request.getPlayerId());
        }

        Resources fromPlayerResources = resources.get();
        if(fromPlayerResources.hasEnoughResources(request.getOffer(), 1)) {
            Set<User> players = gameRepository.findById(request.getGameId()).get().getPlayers();
            if(players.isEmpty()) {
                throw new NoPlayerFoundException("GameId: " + request.getGameId());
            }
            for(User player : players) {
                if(player.getId().equals(request.getPlayerId()))
                    continue;
                Optional<Resources> toResources = resourceRepository.findByGameIdAndPlayerId(request.getGameId(), player.getId());
                if (toResources.isEmpty()) {
                    logger.error("Resources not found, Game ID: " + request.getGameId() + ", Player ID: " + player.getId());
                    throw new IllegalArgumentException("Resources not found, Game ID: " + request.getGameId() + ", Player ID: " + player.getId());
                }
                Resources toPlayerResources = toResources.get();
                if(toPlayerResources.hasEnoughResources(request.getRequest(), 1)) {
                    logger.error("Player " + player.getId() + " has one " + request.getRequest() + " available.");
                    Trade trade = new Trade(request.getGameId(), request.getPlayerId(), player.getId(), request.getOffer(), request.getRequest(), TradeStatus.ACTIVE);
                    tradeRepository.save(trade);
                    return trade;
                }
                else {
                    logger.error("Player " + player.getId() + " doesn't have one " + request.getRequest() + " available.");
                    throw new IllegalArgumentException("Player " + player.getId() + " doesn't have one " + request.getRequest() + " available.");
                }
            }
            logger.error("There are no players having the requested resource.");
            throw new IllegalArgumentException("There are no players having the requested resource.");
            
        }
        else {
            logger.error("Player does not have the offering resources.");
            throw new IllegalArgumentException("Player does not have the offering resources.");
        }
       
    }

    @Transactional
    public TradeStatus merchantTrade(TradeRequest request) {
        if(request == null) {
            logger.error("Request body is null.");
            throw new IllegalArgumentException("Request body is null.");
           
        }

        Optional<Game> game = gameRepository.findById(request.getGameId());
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + request.getGameId());
            throw new GameNotFoundException(request.getGameId());
        }

        if (request.getOffer() == request.getRequest()) {
            logger.error("Offered and requested resource cannot be the same.");
            throw new IllegalArgumentException("Offered and requested resource cannot be the same.");
        }

        Optional<Resources> resources = resourceRepository.findByGameIdAndPlayerId(request.getGameId(), request.getPlayerId());
        if (resources.isEmpty()) {
            logger.error("Resources not found, Game ID: " + request.getGameId() + ", Player ID: " + request.getPlayerId());
            throw new IllegalArgumentException("Resources not found, Game ID: " + request.getGameId() + ", Player ID: " + request.getPlayerId());
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
            throw new IllegalArgumentException("Player has not enough offering resources.");
        }
        
    }

    @Transactional
    public List<Trade> getMyActiveTrades(Long gameId, Long playerId) {
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + gameId);
            throw new GameNotFoundException(gameId);
        }

        List<Trade> activeTrades = tradeRepository.findAll().stream().filter(trade -> trade.getToPlayerId().equals(playerId) && trade.getStatus().equals(TradeStatus.ACTIVE)).toList();
        if (activeTrades.isEmpty()) {
            logger.error("No active trades are available for player " + playerId + " to deal with.");
            throw new IllegalArgumentException("No active trades are available for player " + playerId + " to deal with.");
        }
        return activeTrades;
    }

    @Transactional
    public TradeStatus acceptTrade(Long gameId, Long tradeId) {
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
            logger.error("Resources not found, Game ID: " + gameId + ", Player ID: " + trade.getFromPlayerId());
            return TradeStatus.CANCELLED;
        }

        Optional<Resources> toResources = resourceRepository.findByGameIdAndPlayerId(gameId, trade.getToPlayerId());
        if (toResources.isEmpty()) {
            logger.error("Resources not found, Game ID: " + gameId + ", Player ID: " + trade.getToPlayerId());
            return TradeStatus.CANCELLED;
        }

        Resources fromPlayerResources = fromResources.get();
        Resources toPlayerResources = toResources.get();
        if(fromPlayerResources.hasEnoughResources(trade.getOffer(), 1) && toPlayerResources.hasEnoughResources(trade.getRequest(), 1)) {
            fromPlayerResources.subtract(trade.getOffer(), 1);
            fromPlayerResources.add(trade.getRequest(), 1);
            toPlayerResources.subtract(trade.getRequest(), 1);
            toPlayerResources.add(trade.getOffer(), 1);
            resourceRepository.save(fromPlayerResources);
            resourceRepository.save(toPlayerResources);
            tradeRepository.delete(trade);
            logger.error("Trade was completed successfully.");
            return TradeStatus.COMPLETED;
        }
        else {
            logger.error("Players have no longer enough resources to trade.");
        }
        return TradeStatus.CANCELLED;
    }


    // Still to be discussed
    @Transactional
    public Trade declineTrade(Long gameId, Long tradeId) {
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + gameId);
            return null;
        }

        Optional<Trade> optionalTrade = tradeRepository.findByGameIdAndTradeId(gameId, tradeId);
        if (optionalTrade.isEmpty()) {
            logger.error("Trade not found, ID: " + tradeId);
            return null;
        }

        Trade trade = optionalTrade.get();
        tradeRepository.delete(trade);

        return trade;

    }

    



    
}
