package com.example.gameserver.services;
import com.example.gameserver.api.dto.BuildingCreateRequest;
import com.example.gameserver.entity.Building;
import com.example.gameserver.enums.BuildingType;
import com.example.gameserver.repository.BuildingRepository;
import com.example.gameserver.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BuildingService {
    private static final Logger logger = LoggerFactory.getLogger(BuildingService.class);
    private final BuildingRepository buildingRepository;
    private final GameRepository gameRepository;

    @Autowired
    public BuildingService(BuildingRepository buildingRepository, GameRepository gameRepository) {
        this.buildingRepository = buildingRepository;
        this.gameRepository = gameRepository;
    }

    //construct
    public Building constructBuilding(String playerId, String gameId, BuildingCreateRequest request) {
        if(playerId == null || gameId == null || request == null) {
            return null;
        }
        // TO DO: Implement logic to check that player exists

        if(gameRepository.findById(gameId).isEmpty()) {
            return null;
        }

        Building building = new Building(gameId, playerId, request.getType());
        buildingRepository.save(building);
        return building;
    }

    public List<Building> getBuildings(String playerId, String gameId) {
        if(playerId == null || gameId == null) {
            return null;
        }

        // TO DO: Implement logic to check that player exists

        List<Building> playerBuildings = buildingRepository.findAll().stream().filter(building -> building.getPlayerId().equals(playerId) && building.getGameId().equals(gameId)).toList();
        if(playerBuildings.isEmpty()) {
            return null;
        }

      return playerBuildings;
    }

    public List<BuildingType> getAvailableBuildingTypes(String playerId, String gameId) {
        // TO IMPLEMENT WHEN player and resources are implemented
        return null;
    }

    public void upgradeBuilding(String playerId, String gameId, String buildingId) {
        if(playerId == null || gameId == null || buildingId == null) {
            logger.error("There is a problem with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId + " BuildingId: " + buildingId);
            return;
        }
        //May need to check if player has enough resources to upgrade
        Optional<Building> buildingToUpgrade = buildingRepository.findAll().stream().filter(building -> building.getPlayerId().equals(playerId) && building.getGameId().equals(gameId) && building.getId().equals(buildingId)).findFirst();
        if(buildingToUpgrade.isEmpty()) {
            logger.error("Building not found with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId + " BuildingId: " + buildingId);
            return;
        }
        switch (buildingToUpgrade.get().getType()) {
            case SETTLEMENT:
                buildingToUpgrade.get().setType(BuildingType.TOWN);
                break;
            case TOWN:
                buildingToUpgrade.get().setType(BuildingType.CASTLE);
                break;
            default:
                break;
        }
        buildingRepository.save(buildingToUpgrade.get());
    }

    public Building buildingInfo(String playerId, String gameId, String buildingId) {
        if(playerId == null || gameId == null || buildingId == null) {
            logger.error("There is a problem with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId + " BuildingId: " + buildingId);
            return null;
        }

        Optional<Building> building = buildingRepository.findAll().stream().filter(b -> b.getPlayerId().equals(playerId) && b.getGameId().equals(gameId) && b.getId().equals(buildingId)).findFirst();
        if(building.isEmpty()) {
            logger.error("Building not found with the given Id's: GameId: " + gameId +  " PlayerId: " + playerId + " BuildingId: " + buildingId);
            return null;
        }
        return building.orElse(null);
    }
}
