package com.example.gameserver.controllers;

import com.example.gameserver.aggregates.GameCreateRequest;
import com.example.gameserver.aggregates.PlayerJoinRequest;
import com.example.gameserver.exceptions.GameFullException;
import com.example.gameserver.exceptions.GameNotFoundException;
import com.example.gameserver.exceptions.InvalidGameStateException;
import com.example.gameserver.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.gameserver.aggregates.Game;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> getGameDetails() {
        return new ResponseEntity<>("Hello project", HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestBody GameCreateRequest request) {
        Game newGame = gameService.createGame(request);
        return new ResponseEntity<>(newGame, HttpStatus.CREATED);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGameDetails(@PathVariable String gameId) {
        com.example.gameserver.aggregates.Game game = gameService.getGameById(gameId);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<Game> joinGame(
            @PathVariable String gameId,
            @RequestBody PlayerJoinRequest request) {
        try {
            Game game = gameService.joinGame(gameId, request);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (GameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (GameFullException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{gameId}/start")
    public ResponseEntity<Game> startGame(@PathVariable String gameId) {
        try {
            Game game = gameService.startGame(gameId);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (GameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidGameStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}












