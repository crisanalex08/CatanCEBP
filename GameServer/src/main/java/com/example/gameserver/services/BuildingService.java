package com.example.gameserver.services;
import com.example.gameserver.aggregates.Building;
import com.example.gameserver.aggregates.BuildingCreateRequest;
import com.example.gameserver.enums.BuildingType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingService {
    public Building constructBuilding(String playerId, String gameId, BuildingCreateRequest request) {
        return null;
    }

    public List<Building> getBuildings(String playerId, String gameId) {
        return null;
    }

    public List<BuildingType> getAvailableBuildingTypes(String playerId, String gameId) {
        return null;
    }

    public void upgradeBuilding(String playerId, String gameId, String buildingId) {
        //To be implemented
    }

    public Building BuildingInfo(String playerId, String gameId, String buildingId) {
        return null;
    }
}
