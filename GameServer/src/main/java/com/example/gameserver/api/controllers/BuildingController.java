package com.example.gameserver.api.controllers;

import com.example.gameserver.api.dto.BuildingCreateRequest;
import com.example.gameserver.entity.Building;
import com.example.gameserver.enums.BuildingType;
import com.example.gameserver.services.BuildingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api/games/{gameId}/Buildings")
@Tag(name = "Building Controller", description = "Operations to manage buildings")
public class BuildingController {

    public final BuildingService buildingService;

    @Autowired
    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    // No inter-service communication required
    @GetMapping("/{playerId}/buildings")
    public ResponseEntity<List<Building>> getPlayerBuildings(@PathVariable Long gameId, @PathVariable Long playerId) {
        

        Future<List<Building>> futurePlayerBuildings = buildingService.getBuildings(playerId, gameId);
        try{
            List<Building> playerBuildings = futurePlayerBuildings.get();
            if (playerBuildings == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(playerBuildings, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // This needs to communicate with ResourceService to get player resources
    @PostMapping("/{playerId}/construct")
    public ResponseEntity<Building> constructBuilding(@PathVariable Long gameId, @PathVariable Long playerId, @RequestBody BuildingCreateRequest buildingCreateRequest) {
        Building newBuilding = buildingService.constructBuilding(playerId, gameId);
        if (newBuilding == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(newBuilding, HttpStatus.CREATED);
    }

    // This may need to communicate with ResourceService to get player resources
    @PostMapping("/{playerId}/{buildingId}/upgrade")
    public ResponseEntity<BuildingType> upgradeBuilding(@PathVariable Long gameId, @PathVariable Long playerId, @PathVariable Long buildingId) {
        BuildingType upgradedType =  buildingService.upgradeBuilding(playerId, gameId, buildingId);
        if (upgradedType == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        return new ResponseEntity<>(upgradedType, HttpStatus.OK);
    }


    // This will need to communicate with ResourceService to get player resources
    // in order to determine which buildings can be constructed
    @GetMapping("/{playerId}/available")
    public ResponseEntity<List<BuildingType>> getAvailableBuildingTypes(@PathVariable Long gameId, @PathVariable Long playerId) {
        List<BuildingType> availableBuildingTypes = buildingService.getAvailableBuildingTypes(gameId, playerId);
        if (availableBuildingTypes == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(availableBuildingTypes, HttpStatus.OK);
    }

    //No inter-service communication required
    @GetMapping("/{playerId}/{buildingId}/info")
    public ResponseEntity<Building> getBuildingInfo(@PathVariable Long gameId, @PathVariable Long playerId, @PathVariable Long buildingId) {
        Future<Building> futureBuilding = buildingService.getBuildingInfo(playerId, gameId, buildingId);
        try{
            Building building = futureBuilding.get();

            return new ResponseEntity<>(building, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      
      
    }

}
