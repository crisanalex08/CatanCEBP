package com.example.gameserver.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gameserver.entity.Building;
import com.example.gameserver.entity.Game;
import com.example.gameserver.entity.User;
import com.example.gameserver.enums.ResourceType;
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
      
        resourceService.initializePlayerResources(gameId);
        buildingService.initializePlayerBuildings(gameId);
        
        
        return game;
    }
    //Roll the dice and produce resources for all buildings
    @Transactional
    public String rollDiceAndDistributeResources(Long gameId, Long PlayerId)
    {
         // Roll the dice to get the dice value between 1 to 6 (inclusive)
         int diceValue = (int) (Math.random() * 6 + 1);
         // int diceValue = 1; // for testing

        if(gameId == null || PlayerId == null)
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
                List<Building> buildings = buildingService.getBuildings(player.getId(), gameId).get();
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
            
        }
        catch(Exception e)
        {
            log.error("Error while distributing resources: {}", e.getMessage());
            return "Error while distributing resources";
        }
        return "Resources distributed successfully for players when dice value is: " + diceValue + " rolled by player: " + PlayerId;
}

    

    


}
    
