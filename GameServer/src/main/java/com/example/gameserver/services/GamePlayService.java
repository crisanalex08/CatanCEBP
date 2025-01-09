package com.example.gameserver.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.gameserver.entity.Building;
import com.example.gameserver.entity.Game;
import com.example.gameserver.entity.User;
import com.example.gameserver.enums.BuildingType;
import com.example.gameserver.enums.ResourceType;
import com.example.gameserver.models.DiceRollResponse;
import com.example.gameserver.models.ProductionData;
import com.example.gameserver.repository.BuildingRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GamePlayService {
    private final GameService gameService;
    private final ResourceService resourceService;
    private final BuildingService buildingService;


    @Autowired
    public GamePlayService(
            GameService gameService,
            ResourceService resourceService,
            BuildingService buildingService,
            TradeService tradeService,
            BuildingRepository buildingRepository

            ) {
        this.gameService = gameService;
        this.resourceService = resourceService;
        this.buildingService = buildingService;

    }

    @Transactional
    public Game initializeGame(Long gameId) {
        
        log.info("Initializing game: {}", gameId);
        Game game = gameService.startGame(gameId);
        
        // Initialize resources for all players
      
        resourceService.initializePlayerResources(gameId);
        buildingService.initializePlayerBuildings(gameId);
        
        
        return game;
    }
    //Roll the dice and produce resources for all buildings
    @Transactional
    public DiceRollResponse rollDiceAndDistributeResources(Long gameId, Long playerId)
    {
         // Roll the dice to get the dice value between 1 to 6 (inclusive)
         int diceValue = (int) (Math.random() * 6 + 1);

        if(gameId == null || playerId == null)
        {
            throw new IllegalArgumentException("GameId and PlayerId cannot be null");
        }
        try{
            //Check if the game exists
            Game game = gameService.getGameById(gameId).get();
            if(game == null)
            {
                throw new IllegalArgumentException("Game not found with ID: " + gameId);
            }

            //Get all the players in the game
            Set<User> players = game.getPlayers();
            if(players == null || players.isEmpty())
            {
                throw new IllegalArgumentException("No players found in the game: " + gameId);
            }

            //Iterate through all the players and their buildings to distribute resources
            for (User player : players) 
            {
                List<Building> buildings = buildingService.getBuildingsTransactional(gameId, player.getId());
                System.out.println("Buildings: " + buildings);
                if(buildings == null || buildings.isEmpty())
                {
                    throw new IllegalArgumentException("No buildings found for player: " + player.getId());
                }

                for (Building building : buildings) 
                {
                    //Iterate through all production data ( A building can produce multiple resources with different dice values)
                    for (ProductionData productionData : building.getProduction()) 
                    {
                        if (productionData.getDiceValue() == diceValue) 
                        {
                            Map<ResourceType, Integer> resources = new HashMap<>();
                            resources.put(productionData.getResourceType(), productionData.getProductionRate());
                            resourceService.addResource(gameId, player.getId(), resources);
                        }
                    }
                }
            }
            
            return new DiceRollResponse(diceValue, gameId, playerId, "Resources distributed successfully", true, null);
        }
        catch(Exception e)
        {
            log.error("Error while distributing resources: {}", e.getMessage());
            return new DiceRollResponse(diceValue, gameId, playerId, "Failed to distribute resources", false, e.getMessage());
        }
       
}

    @Async
    public CompletableFuture<Building> constructBuilding(Long playerId, Long gameId){
        return CompletableFuture.completedFuture(buildingService.constructBuilding(gameId, playerId));
    }

    
    @Async
    public CompletableFuture<Building> upgradeBuilding(Long gameId, Long playerId, Long buildingId) {
        try {
            Building building = buildingService.getBuildingInfo(gameId, playerId, buildingId).get();
            
            if (building == null) {
                throw new IllegalArgumentException("Building not found with ID: " + buildingId);
            }

            if (building.getPlayerId().longValue() != playerId) {
                throw new IllegalArgumentException("Building does not belong to player: " + playerId);
            }

            if (building.getType() == BuildingType.CASTLE) {
                throw new IllegalArgumentException("Building is already at max level");
            }

            Building upgradedBuilding =  buildingService.upgradeBuilding(gameId, playerId, buildingId);

            if (upgradedBuilding == null) {
                throw new IllegalArgumentException("Failed to upgrade building");
            }
            return CompletableFuture.completedFuture(upgradedBuilding);

        } catch (Exception e) {
            log.error("Error while upgrading building: {}", e.getMessage());
            System.out.println(e.getMessage());
            throw new IllegalArgumentException("Failed to upgrade building because: " + e.getMessage());
        }
    }
    
}
