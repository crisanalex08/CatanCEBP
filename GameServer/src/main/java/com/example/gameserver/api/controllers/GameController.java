package com.example.gameserver.api.controllers;

import com.example.gameserver.api.dto.GameCreateRequest;
import com.example.gameserver.api.dto.PlayerJoinRequest;
import com.example.gameserver.entity.Game;
import com.example.gameserver.exceptions.GameFullException;
import com.example.gameserver.exceptions.GameNotFoundException;
import com.example.gameserver.exceptions.InvalidGameStateException;
import com.example.gameserver.services.GameService;
import com.example.gameserver.websocket.GamesWebSocketHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;

@CrossOrigin
@RestController
@RequestMapping("/api/games")
@Tag(name = "Game Controller", description = "Operations to manage games")
public class GameController {

    private final GameService gameService;

    @Autowired
    private final GamesWebSocketHandler gamesWebSocketHandler;
    @Autowired
    public GameController(GameService gameService, GamesWebSocketHandler gamesWebSocketHandler) {
        this.gameService = gameService;
        this.gamesWebSocketHandler = gamesWebSocketHandler;

    }

 
    @Operation(summary = "Create a new game")
    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestBody GameCreateRequest request) {
        Game newGame = gameService.createGame(request);
        gamesWebSocketHandler.broadcastGameList();
        
        
        return new ResponseEntity<>(newGame, HttpStatus.CREATED);
    }

    @Operation(summary = "Get game details")
    @GetMapping("/{gameId}")
    public ResponseEntity<?> getGameDetails(@PathVariable Long gameId) {
        try {
            Future<Game> futureGame = gameService.getGameById(gameId);
            Game game = futureGame.get();
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (Exception e) {
            
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
       
    }

    @Operation(summary = "Join a game")
    @PostMapping("/{gameId}/join")
    public ResponseEntity<?> joinGame(
            @PathVariable Long gameId,
            @RequestBody  PlayerJoinRequest request) {
        try {
            Game game = gameService.joinGame(gameId, request);
            if(game == null) {
                return new ResponseEntity<>("Player already exists!",HttpStatus.NOT_FOUND);
            }
            gamesWebSocketHandler.broadcastToLobby(gameId.toString(), new TextMessage("Player Joined"));
            gamesWebSocketHandler.broadcastGameList();
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (GameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (GameFullException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "Leave a game")
    @PutMapping("/{gameId}/leave")
    public ResponseEntity<?> leaveGame(
            @PathVariable Long gameId,
            @RequestBody  PlayerJoinRequest request) {
        try {
            gameService.leaveGame(gameId, request);
            gamesWebSocketHandler.broadcastToLobby(gameId.toString(), new TextMessage("Player Left"));
            gamesWebSocketHandler.broadcastGameList();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (GameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (GameFullException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Start a game")
    @PostMapping("/{gameId}/start")
    public ResponseEntity<?> startGame(@PathVariable Long gameId) {
        try {
            Game game = gameService.startGame(gameId);
            


            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (GameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidGameStateException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "List all games")
    @GetMapping("/list")
    public ResponseEntity<?> listGames() {
       try {
              Future<Iterable<Game>> futureGames = gameService.listGames();
              Iterable<Game> games = futureGames.get();
              return new ResponseEntity<>(games, HttpStatus.OK);
       } catch (Exception e) {
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
       }

    }

}












