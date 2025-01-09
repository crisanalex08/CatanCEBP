package com.example.gameserver.api.controllers;

import java.util.concurrent.Future;

import com.example.gameserver.websocket.GamesWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import com.example.gameserver.api.dto.GameMessage;
import com.example.gameserver.entity.Building;
import com.example.gameserver.entity.Game;
import com.example.gameserver.entity.User;
import com.example.gameserver.models.DiceRollResponse;
import com.example.gameserver.services.BuildingService;
import com.example.gameserver.services.GamePlayService;
import com.example.gameserver.services.GameService;
import com.example.gameserver.services.ResourceService;
import com.example.gameserver.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@CrossOrigin
@RestController
@Tag (name = "Game Play Controller", description = "Operations to manage game play")
@RequestMapping("/api/gameplay")
@Slf4j
public class GamePlayController {
    private final GamePlayService gamePlayService;
    private final GameService gameService;
    private final UserService userService;
    private final ResourceService resourceService;
    private final BuildingService buildingService;
    private final GamesWebSocketHandler gamesWebSocketHandler;


    @Autowired
    public GamePlayController(GamePlayService gamePlayService, ResourceService resourceService, BuildingService buildingService, GamesWebSocketHandler gamesWebSocketHandler, GameService gameService, UserService userService) {
        this.gamePlayService = gamePlayService;
        this.buildingService = buildingService;
        this.resourceService = resourceService;
        this.gameService = gameService;
        this.userService = userService;
        this.gamesWebSocketHandler = gamesWebSocketHandler;
    }

    @Operation(summary = "Start game and initialize player resources and buildings")
    @PostMapping("/{gameId}/start")
    public ResponseEntity<?> startGame(@PathVariable Long gameId) {
        try {
            Game result = gamePlayService.initializeGame(gameId);
            gamesWebSocketHandler.broadcastToLobby(gameId.toString(), new TextMessage("Game Started"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error starting game", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Roll the dice and distribute resources")
    @PostMapping("/{gameId}/roll/{playerId}")
    public ResponseEntity<?> rollDiceAndDistributeResources(@PathVariable Long gameId, @PathVariable Long playerId) {
        try {
            DiceRollResponse result = gamePlayService.rollDiceAndDistributeResources(gameId, playerId);
            String userName = getUserName(playerId);
            if(result.isSuccess()) {
                sendSystemMessage(gameId, "Player " + userName + " rolled " + result.getDiceRoll());
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error rolling dice", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Operation(summary = "Construct a building")
    @PostMapping("/{gameId}/construct/{playerId}")
    public ResponseEntity<?> constructBuilding(@PathVariable Long gameId, @PathVariable Long playerId) {
        try {
            CompletableFuture<Building> building = gamePlayService.constructBuilding(playerId, gameId);
            String userName = getUserName(playerId);
            if(building.get() != null) {
                sendSystemMessage(gameId, "Player " + userName + " constructed " + building.get().getType());
            }
            return ResponseEntity.ok(building.get());
        } catch (Exception e) {
            log.error("Error constructing building", e);
            String message = e.getMessage().split(":")[1];
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



    @Operation(summary = "Upgrade building")
    @PostMapping("/{gameId}/upgrade/{playerId}/{buildingId}")
    public ResponseEntity<?> upgradeBuilding(@PathVariable Long gameId, @PathVariable Long playerId, @PathVariable Long buildingId) {
        try {
            Future<String> result = gamePlayService.upgradeBuilding(gameId, playerId, buildingId);

            String userName = getUserName(playerId);
          

            String upgradedBuilding = result.get();
            System.out.println(upgradedBuilding);
            switch (upgradedBuilding){
                
                case "Castle":
                    sendSystemMessage(gameId, "Player " + userName + " has upgraded a Town to Castle");
                    sendSystemMessage(gameId, "GameWon by Player: " + userName);
                    gameService.endGame(gameId);
                    buildingService.clearBuildings(gameId);
                    resourceService.clearResources(gameId);
                    break;
                case "Town":
                    sendSystemMessage(gameId, "Player " + userName + " has upgraded a Settlement to Town");
                    break;
                
            }
            return ResponseEntity.ok(upgradedBuilding);
        } catch (Exception e) {
            log.error("Error upgrading building", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get players buildings")
    @GetMapping("/{gameId}/buildings")
    public ResponseEntity<?> getBuildings(@PathVariable Long gameId) {
        try {
            Future<List<Building>> result = buildingService.getAllBuildings(gameId);
            return ResponseEntity.ok(result.get());
        } catch (Exception e) {
            log.error("Error getting buildings", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Send a system message to the game lobby(Helper method)
    private void sendSystemMessage(Long gameId, String content){
        GameMessage message = new GameMessage();
        message.setGameId(gameId);
        message.setSender("System");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try {
            String jsonMessage = mapper.writeValueAsString(message);
            gamesWebSocketHandler.broadcastToLobby(gameId.toString(), new TextMessage(jsonMessage));
        }
        catch (Exception e) {
            log.error("GamePlayController: Error sending system message, could not map the message to string! ", e);
        }
    }

    private String getUserName(Long playerId) {
        User user = userService.getUserById(playerId);
        return user.getName();
    }

}
