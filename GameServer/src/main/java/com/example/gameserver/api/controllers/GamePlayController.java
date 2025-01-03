package com.example.gameserver.api.controllers;

import java.util.concurrent.Future;

import com.example.gameserver.websocket.GamesWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gameserver.entity.Building;
import com.example.gameserver.entity.Game;
import com.example.gameserver.services.BuildingService;
import com.example.gameserver.services.GamePlayService;
import com.example.gameserver.services.GameService;
import com.example.gameserver.services.ResourceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;

import java.util.concurrent.CompletableFuture;

@CrossOrigin
@RestController
@Tag (name = "Game Play Controller", description = "Operations to manage game play")
@RequestMapping("/api/gameplay")
@Slf4j
public class GamePlayController {
    private final GamePlayService gamePlayService;
     private final GameService gameService;
    private final ResourceService resourceService;
    private final BuildingService buildingService;
    private final GamesWebSocketHandler gamesWebSocketHandler;


    @Autowired
    public GamePlayController(GamePlayService gamePlayService, ResourceService resourceService, BuildingService buildingService, GamesWebSocketHandler gamesWebSocketHandler, GameService gameService) {
        this.gamePlayService = gamePlayService;
        this.resourceService = resourceService;
        this.buildingService = buildingService;
        this.gameService = gameService;
        this.gamesWebSocketHandler = gamesWebSocketHandler;
    }

    @Operation(summary = "Start game and initialize player resources and buildings")
    @PostMapping("/{gameId}/start")
    public ResponseEntity<?> startGame(@PathVariable Long gameId) {
        try {
            Game result = gamePlayService.initializeGame(gameId);
            gamesWebSocketHandler.broadcastToLobby(gameId.toString(), new TextMessage("AvailableBuildings"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error starting game", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Roll the dice and distribute resources")
    @PostMapping("/{gameId}/roll/{playerId}")
    public ResponseEntity<?> rollDiceAndDistributeResources(@PathVariable Long gameId, @PathVariable Long playerId) {
        try {
            String result = gamePlayService.rollDiceAndDistributeResources(gameId, playerId);
            gamesWebSocketHandler.broadcastToLobby(gameId.toString(), new TextMessage("AvailableBuildings"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error rolling dice", e);
            return ResponseEntity.badRequest().build();
        }
    }


    @Operation(summary = "Construct a building")
    @PostMapping("/{gameId}/construct/{playerId}")
    public ResponseEntity<?> constructBuilding(@PathVariable Long gameId, @PathVariable Long playerId) {
        try {
            CompletableFuture<Building> building = gamePlayService.constructBuilding(playerId, gameId);
            gamesWebSocketHandler.broadcastToLobby(gameId.toString(), new TextMessage("AvailableBuildings"));
            return ResponseEntity.ok(building.get());
        } catch (Exception e) {
            log.error("Error constructing building", e);
            return ResponseEntity.internalServerError()
                    .body("Failed to construct building because: " + e.getMessage());
        }

    }

    @Operation(summary = "Upgrade building")
    @PostMapping("/{gameId}/upgrade/{playerId}/{buildingId}")
    public ResponseEntity<?> upgradeBuilding(@PathVariable Long gameId, @PathVariable Long playerId, @PathVariable Long buildingId) {
        try {
            Future<String> result = gamePlayService.upgradeBuilding(gameId, playerId, buildingId);
            if(result.get().equals("WIN")){
                gamesWebSocketHandler.broadcastToLobby(gameId.toString(), new TextMessage("GameWon by Player: " + playerId));
                gameService.endGame(gameId);
                buildingService.clearBuildings(gameId);
                resourceService.clearResources(gameId);
            }else {
                gamesWebSocketHandler.broadcastToLobby(gameId.toString(), new TextMessage("AvailableBuildings"));
            }
            return ResponseEntity.ok(result.get());
        } catch (Exception e) {
            log.error("Error upgrading building", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    
}
