package com.example.gameserver.services;
import com.example.gameserver.api.dto.BuildingCreateRequest;
import com.example.gameserver.entity.Building;
import com.example.gameserver.entity.Resources;
import com.example.gameserver.entity.User;
import com.example.gameserver.enums.BuildingType;
import com.example.gameserver.enums.ResourceType;
import com.example.gameserver.exceptions.*;
import com.example.gameserver.repository.BuildingRepository;
import com.example.gameserver.repository.GameRepository;

import com.example.gameserver.repository.ResourceRepository;
import com.example.gameserver.repository.UsersRepository;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class BuildingService {
    private static final Logger logger = LoggerFactory.getLogger(BuildingService.class);
    private final BuildingRepository buildingRepository;
    private final GameRepository gameRepository;
    private final ResourceRepository resourceRepository;
    private final UsersRepository userRepository;

    @Autowired
    public BuildingService(BuildingRepository buildingRepository, GameRepository gameRepository, ResourceRepository resourceRepository, UsersRepository userRepository) {
        this.buildingRepository = buildingRepository;
        this.gameRepository = gameRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public List<Building> initializePlayerBuildings (Long gameId) {
        if(gameId == null) {
            throw new NullValueException("gameId: "+ gameId + "| is null");
        }

        if(gameRepository.findById(gameId).isEmpty()) {
            throw new GameNotFoundException(gameId);
        }

        Set<User> players = userRepository.findAllByGameId(gameId);
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
    public Building constructBuilding(Long playerId, Long gameId, BuildingCreateRequest request) {
        if(playerId == null || gameId == null || request == null) {
            throw new NullValueException("playerId: "+ playerId + ", gameId: " + gameId + ", or request: " + request + "| is null");
        }
        // TO DO: Implement logic to check that player exists
    

        if(gameRepository.findById(gameId).isEmpty()) {
            throw new GameNotFoundException(gameId);
        }

        long playerBuildingCount = buildingRepository.findAll().stream().filter(building -> building.getPlayerId().equals(playerId) && building.getGameId().equals(gameId)).count();
        if(playerBuildingCount >= 3) {
            throw new PlayerHasTheMaximumAmountOfBuildings();
        }
        if(playerBuildingCount > 0) {
            Resources playerResources = resourceRepository.findByGameIdAndPlayerId(gameId, playerId).orElseThrow(() -> new NoResourceFoundException("GameId: " + gameId + " PlayerId: " + playerId));
            if(!playerResources.hasEnoughResourcesToBuild(BuildingType.SETTLEMENT)) {
                throw new NotEnoughResourcesException();
            }

            playerResources.subtract(ResourceType.WOOD, 1);
            playerResources.subtract(ResourceType.CLAY, 1);
            playerResources.subtract(ResourceType.WHEAT, 1);
            resourceRepository.save(playerResources);
        }

        Building building = new Building(gameId, playerId, BuildingType.SETTLEMENT);
        buildingRepository.save(building);
        return building;
    }

    @Async
    public Future<List<Building>> getBuildings(Long playerId, Long gameId) {
        if(playerId == null || gameId == null) {
            throw new NullValueException("playerId: "+ playerId + ", gameId: " + gameId + "| is null");
        }

        // TO DO: Implement logic to check that player exists

        List<Building> playerBuildings = buildingRepository.findAll().stream().filter(building -> building.getPlayerId().equals(playerId) && building.getGameId().equals(gameId)).toList();
        if(playerBuildings.isEmpty()) {
            throw new NoBuildingFoundException("GameId: " + gameId +  " PlayerId: " + playerId);
        }

      return CompletableFuture.completedFuture(playerBuildings);
    }

// TO BE IMPLEMENTED
    public List<BuildingType> getAvailableBuildingTypes(Long playerId, Long gameId) {
        if(playerId == null || gameId == null) {
            logger.error("There is a problem with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId);
            throw new NullValueException("playerId: "+ playerId + ", gameId: " + gameId + "| is null");
        }

//        Resources playerResources =  resourceRepository.findByGameIdAndPlayerId(gameId, playerId).orElseThrow(() -> new NoResourceFoundException("GameId: " + gameId +  " PlayerId: " + playerId));
//        List<Building> playerBuildings = buildingRepository.findAll().stream().filter(building -> building.getPlayerId().equals(playerId) && building.getGameId().equals(gameId)).toList();

        return null;
    }

    @Transactional
    public BuildingType upgradeBuilding(Long playerId, Long gameId, Long buildingId) {
        if(playerId == null || gameId == null || buildingId == null) {
            logger.error("There is a problem with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId + " BuildingId: " + buildingId);
            throw new NullValueException("playerId: "+ playerId + ", gameId: " + gameId + ", or buildingId: " + buildingId + "| is null");
        }

        //May need to check if player has enough resources to upgrade

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
            case SETTLEMENT:
                buildingToUpgrade.get().setType(BuildingType.TOWN);

                playerResources.subtract(ResourceType.WOOD, 2);
                playerResources.subtract(ResourceType.CLAY, 2);
                playerResources.subtract(ResourceType.WHEAT, 2);

                resourceRepository.save(playerResources);
                buildingRepository.save(buildingToUpgrade.get());
                return BuildingType.TOWN;
            case TOWN:
                buildingToUpgrade.get().setType(BuildingType.CASTLE);

                playerResources.subtract(ResourceType.WOOD, 3);
                playerResources.subtract(ResourceType.STONE, 3);
                playerResources.subtract(ResourceType.SHEEP, 3);
                playerResources.subtract(ResourceType.GOLD, 3);

                resourceRepository.save(playerResources);
                buildingRepository.save(buildingToUpgrade.get());
                return BuildingType.CASTLE;
            default:
                logger.error("Building is already at max level");
                return null;
        }
    }
    @Async
    public Future<Building> getBuildingInfo(Long playerId, Long gameId, Long buildingId) {
        if(playerId == null || gameId == null || buildingId == null) {
            logger.error("There is a problem with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId + " BuildingId: " + buildingId);
            throw new NullValueException("playerId: "+ playerId + ", gameId: " + gameId + ", or buildingId: " + buildingId + "| is null");
        }

        Optional<Building> building = buildingRepository.findAll().stream().filter(b -> b.getPlayerId().equals(playerId) && b.getGameId().equals(gameId) && b.getId().equals(buildingId)).findFirst();
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

}
