package com.example.gameserver.api.controllers;

import com.example.gameserver.api.dto.GameCreateRequest;
import com.example.gameserver.api.dto.PlayerJoinRequest;
import com.example.gameserver.entity.Game;
import com.example.gameserver.exceptions.GameFullException;
import com.example.gameserver.exceptions.GameNotFoundException;
import com.example.gameserver.exceptions.InvalidGameStateException;
import com.example.gameserver.services.GameService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@Tag(name = "Game Controller", description = "Operations to manage games")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

 
    @Operation(summary = "Create a new game")
    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestBody GameCreateRequest request) {
        
        Game newGame = gameService.createGame(request);
        return new ResponseEntity<>(newGame, HttpStatus.CREATED);
    }

    @Operation(summary = "Get game details")
    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGameDetails(@PathVariable Long gameId) {
        try {
            Game game = gameService.getGameById(gameId);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (GameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
       
    }

    @Operation(summary = "Join a game")
    @PostMapping("/{gameId}/join")
    public ResponseEntity<?> joinGame(
            @PathVariable Long gameId,
            @RequestBody PlayerJoinRequest request) {
        try {
            Game game = gameService.joinGame(gameId, request);
            return new ResponseEntity<>(game, HttpStatus.OK);
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
}












