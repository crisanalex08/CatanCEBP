package com.example.gameserver.services;
import com.example.gameserver.entity.Building;
import com.example.gameserver.entity.Game;
import com.example.gameserver.entity.Resources;
import com.example.gameserver.entity.User;
import com.example.gameserver.enums.BuildingType;
import com.example.gameserver.enums.GameStatus;
import com.example.gameserver.enums.ResourceType;
import com.example.gameserver.exceptions.*;
import com.example.gameserver.models.ProductionData;
import com.example.gameserver.repository.BuildingRepository;
import com.example.gameserver.repository.GameRepository;

import com.example.gameserver.repository.ResourceRepository;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class BuildingService {
    private static final Logger logger = LoggerFactory.getLogger(BuildingService.class);
    private final BuildingRepository buildingRepository;
    private final GameRepository gameRepository;
    private final ResourceRepository resourceRepository;

    @Autowired
    public BuildingService(BuildingRepository buildingRepository, GameRepository gameRepository, ResourceRepository resourceRepository) {
        this.buildingRepository = buildingRepository;
        this.gameRepository = gameRepository;
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public List<Building> initializePlayerBuildings (Long gameId) {
        if(gameId == null) {
            throw new NullValueException("gameId: "+ gameId + "| is null");
        }

        if(gameRepository.findById(gameId).isEmpty()) {
            throw new GameNotFoundException(gameId);
        }

        Set<User> players = gameRepository.findById(gameId).get().getPlayers();
        if(players.isEmpty()) {
            throw new NoPlayerFoundException("GameId: " + gameId);
        }

        for(User player : players) {
            Building building = new Building(gameId, player.getId(), BuildingType.SETTLEMENT);
            buildingRepository.save(building);
        }

        return buildingRepository.findAll().stream().filter(building -> building.getGameId().equals(gameId)).toList();
    }

    //construct
    @Transactional
    public Building constructBuilding(Long gameId, Long playerId) {
        if(playerId == null || gameId == null) {
            logger.error("There is a problem with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId);
            throw new NullValueException("playerId: "+ playerId + ", gameId: " + gameId + " is null");
        }

        validateGameAndPlayer(gameId, playerId);

        long playerBuildingCount = getPlayerBuildingCount(playerId, gameId);

        if(playerBuildingCount>= 3) {
            throw new PlayerHasTheMaximumAmountOfBuildings();
        }

        if(playerBuildingCount > 0) {
            Resources playerResources = resourceRepository.findByGameIdAndPlayerId(gameId, playerId).orElseThrow(() -> new NoResourceFoundException("GameId: " + gameId + " PlayerId: " + playerId));
            if(playerResources.hasEnoughResourcesToBuild(BuildingType.SETTLEMENT)) {
                playerResources.subtract(ResourceType.WOOD, 1);
                playerResources.subtract(ResourceType.CLAY, 1);
                playerResources.subtract(ResourceType.WHEAT, 1);
                resourceRepository.save(playerResources);
            }else{
                logger.error("Player does not have enough resources to build a settlement" + " GameId: " + gameId + " PlayerId: " + playerId);
                throw new NotEnoughResourcesException();
            }
        }

        Building building = new Building(gameId, playerId, BuildingType.SETTLEMENT);
        buildingRepository.save(building);
        return building;
    }

    //clears the buildings when the game is finished
    @Transactional
    public void clearBuildings(Long gameId) {
        if(gameId == null) {
            throw new NullValueException("gameId: "+ gameId + "| is null");
        }

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));

        if(game.getStatus() != GameStatus.FINISHED) {
            throw new GameNotFinishedException(gameId);
        }

        buildingRepository.deleteAll(buildingRepository.findAll().stream().filter(building -> building.getGameId().equals(gameId)).toList());
        buildingRepository.flush();
    }


    //used to avoid calling async methods from transactional methods
    @Transactional
    public List<Building> getBuildingsTransactional(Long gameId, Long playerId) {
        if(playerId == null || gameId == null) {
            throw new NullValueException("playerId: "+ playerId + ", gameId: " + gameId + "| is null");
        }

        if(gameRepository.findById(gameId).isEmpty()) {
            throw new GameNotFoundException(gameId);
        }

        if(gameRepository.findById(gameId).get().getPlayerById(playerId) == null) {
            throw new NoPlayerFoundException("PlayerId: " + playerId + " not found in game with GameId: " + gameId);
        }

        return  buildingRepository.findAll().stream().filter(building -> building.getPlayerId().equals(playerId) && building.getGameId().equals(gameId)).toList();
    }

    @Async
    public Future<List<Building>> getBuildings(Long gameId, Long playerId) {
        if(playerId == null || gameId == null) {
            throw new NullValueException("playerId: "+ playerId + ", gameId: " + gameId + "| is null");
        }

        validateGameAndPlayer(gameId, playerId);

        List<Building> playerBuildings = buildingRepository.findAll().stream().filter(building -> building.getPlayerId().equals(playerId) && building.getGameId().equals(gameId)).toList();
        if(playerBuildings.isEmpty()) {
            throw new NoBuildingFoundException("GameId: " + gameId +  " PlayerId: " + playerId);
        }

      return CompletableFuture.completedFuture(playerBuildings);
    }

    @Async 
    public Future<List<Building>> getAllBuildings(Long gameId) {
        if(gameId == null) {
            throw new NullValueException("gameId: "+ gameId + "| is null");
        }

        if(gameRepository.findById(gameId).isEmpty()) {
            throw new GameNotFoundException(gameId);
        }


        return CompletableFuture.completedFuture(buildingRepository.findAll().stream().filter(building -> building.getGameId().equals(gameId)).toList());
    }

    @Async
    public Future<List<BuildingType>> getAvailableBuildingTypes(Long gameId, Long playerId) {
        if(playerId == null || gameId == null) {
            logger.error("There is a problem with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId);
            throw new NullValueException("playerId: "+ playerId + ", gameId: " + gameId + "| is null");
        }

        validateGameAndPlayer(gameId, playerId);

        Resources playerResources =  resourceRepository.findByGameIdAndPlayerId(gameId, playerId).orElseThrow(() -> new NoResourceFoundException("GameId: " + gameId +  " PlayerId: " + playerId));

        if(playerResources == null) {
            logger.error("Resources not found for player: " + playerId + " in game: " + gameId);
            throw new NoResourceFoundException("GameId: " + gameId +  " PlayerId: " + playerId);
        }

        List<BuildingType> availableBuildingTypes = new ArrayList<>();

        long playerBuildingCount = getPlayerBuildingCount(playerId, gameId);
        if(playerBuildingCount < 3 && playerResources.hasEnoughResourcesToBuild(BuildingType.SETTLEMENT)){
            availableBuildingTypes.add(BuildingType.SETTLEMENT);
        }
        if(playerResources.hasEnoughResourcesToBuild(BuildingType.TOWN)){
            availableBuildingTypes.add(BuildingType.TOWN);
        }
        if(playerResources.hasEnoughResourcesToBuild(BuildingType.CASTLE)){
            availableBuildingTypes.add(BuildingType.CASTLE);
        }

        return CompletableFuture.completedFuture(availableBuildingTypes);
    }

    @Transactional
    public BuildingType upgradeBuilding(Long gameId, Long playerId, Long buildingId) {
        if(playerId == null || gameId == null || buildingId == null) {
            logger.error("There is a problem with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId + " BuildingId: " + buildingId);
            throw new NullValueException("playerId: "+ playerId + ", gameId: " + gameId + ", or buildingId: " + buildingId + "| is null");
        }

        validateGameAndPlayer(gameId, playerId);

        Optional<Building> buildingToUpgrade = buildingRepository.findAll().stream().filter(building -> building.getPlayerId().equals(playerId) && building.getGameId().equals(gameId) && building.getId().equals(buildingId)).findFirst();
        if(buildingToUpgrade.isEmpty()) {
            logger.error("Building not found with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId + " BuildingId: " + buildingId);
            throw new NoBuildingFoundException("GameId: " + gameId +  " PlayerId: " + playerId + " BuildingId: " + buildingId);
        }

        Resources playerResources = resourceRepository.findByGameIdAndPlayerId(gameId, playerId).orElseThrow(() -> new NoResourceFoundException("GameId: " + gameId +  " PlayerId: " + playerId));
        if(!playerResources.hasEnoughResourcesToBuild(getNextBuildingType(buildingToUpgrade.get().getType()))) {
            logger.error("Player does not have enough resources to upgrade building");
            throw new NotEnoughResourcesException();
        }

        switch (buildingToUpgrade.get().getType()) {
            case SETTLEMENT: //Current building type is settlement
                playerResources.subtract(ResourceType.WOOD, 2);
                playerResources.subtract(ResourceType.CLAY, 2);
                playerResources.subtract(ResourceType.WHEAT, 2);

                buildingToUpgrade.get().setProduction(setProductionData(BuildingType.TOWN));
                buildingToUpgrade.get().setType(BuildingType.TOWN);
                
                resourceRepository.save(playerResources);
                buildingRepository.save(buildingToUpgrade.get());
                return BuildingType.TOWN;
            case TOWN:  //Current building type is town
                playerResources.subtract(ResourceType.WOOD, 3);
                playerResources.subtract(ResourceType.STONE, 3);
                playerResources.subtract(ResourceType.SHEEP, 3);
                playerResources.subtract(ResourceType.GOLD, 3);

                buildingToUpgrade.get().setType(BuildingType.CASTLE);

                resourceRepository.save(playerResources);
                buildingRepository.save(buildingToUpgrade.get());
                return BuildingType.CASTLE;
            default:
                logger.error("Building is already at max level");
                return null;
        }
    }
    public List<ProductionData> setProductionData(BuildingType type)
    {
        List<ResourceType> possibleResources = new ArrayList<>();

        List<ProductionData> list = new ArrayList<>();
        if(type == BuildingType.TOWN) {

            possibleResources = Arrays.asList(
                ResourceType.STONE, 
                ResourceType.SHEEP, 
                ResourceType.GOLD
            );
        }
            Collections.shuffle(possibleResources);
            List<ResourceType> resources = possibleResources.subList(0, 2);
            
            Random random = new Random();
            Set<Integer> diceValues = new HashSet<>();

            for(ResourceType resource : resources) {
                ProductionData data = new ProductionData();
                data.setResourceType(resource);
                data.setProductionRate(1);
                int diceValue;
                do {
                    diceValue = random.nextInt(6) + 1;
                } while (diceValues.contains(diceValue));
                
                diceValues.add(diceValue);
                data.setDiceValue(diceValue);
                data.setResourceType(resource);
                list.add(data);
                
            }
            return list;
    }

    @Async
    public Future<Building> getBuildingInfo(Long gameId, Long playerId, Long buildingId) {
        if(playerId == null || gameId == null || buildingId == null) {
            logger.error("There is a problem with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId + " BuildingId: " + buildingId);
            throw new NullValueException("playerId: "+ playerId + ", gameId: " + gameId + ", or buildingId: " + buildingId + "| is null");
        }

        Optional<Building> building = buildingRepository.findById(buildingId);
        if(building.isEmpty()) {
            logger.error("Building not found with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId + " BuildingId: " + buildingId);
            throw new NoBuildingFoundException("GameId: " + gameId +  " PlayerId: " + playerId + " BuildingId: " + buildingId);
        }
        return CompletableFuture.completedFuture(building.orElse(null));
    }

    //helper method
    private BuildingType getNextBuildingType(BuildingType type) {
        return switch (type) {
            case SETTLEMENT -> BuildingType.TOWN;
            case TOWN -> BuildingType.CASTLE;
            case CASTLE -> throw new BuildingIsAlreadyAtMaxLevelException();
        };
    }

    //helper method
    private long getPlayerBuildingCount(Long playerId, Long gameId) {
        return buildingRepository.findAll().stream().filter(building -> building.getPlayerId().equals(playerId) && building.getGameId().equals(gameId)).count();
    }

    //helper method
    private void validateGameAndPlayer(Long gameId, Long playerId) {
        Optional<Game> game = gameRepository.findById(gameId);

        if (game.isEmpty()) {
            logger.error("validateGameAndPlayer: Game not found with the given Id: " + gameId);
            throw new GameNotFoundException(gameId);
        }

        if (game.get().getPlayerById(playerId) == null) {
            logger.error("Player not found with the given Id: " + playerId);
            throw new NoPlayerFoundException("PlayerId: " + playerId + " not found in game with GameId: " + gameId);
        }
    }

}
