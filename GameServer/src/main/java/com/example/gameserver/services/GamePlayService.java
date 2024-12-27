package com.example.gameserver.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gameserver.entity.Building;
import com.example.gameserver.entity.Game;
import com.example.gameserver.entity.User;
import com.example.gameserver.models.ProductionData;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GamePlayService {
    private final GameService gameService;
    private final ResourceService resourceService;
    private final BuildingService buildingService;
    private final TradeService tradeService;

    @Autowired
    public GamePlayService(
            GameService gameService,
            ResourceService resourceService,
            BuildingService buildingService,
            TradeService tradeService) {
        this.gameService = gameService;
        this.resourceService = resourceService;
        this.buildingService = buildingService;
        this.tradeService = tradeService;
    }

    @Transactional
    public Game initializeGame(Long gameId) {
        
        log.info("Initializing game: {}", gameId);
        Game game = gameService.startGame(gameId);
        
        // Initialize resources for all players
        for (User player : game.getPlayers()) {
            resourceService.initializePlayerResources(gameId, player.getId());
        }
        
        return game;
    }

    


}
    
