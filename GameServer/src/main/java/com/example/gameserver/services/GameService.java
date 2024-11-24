package com.example.gameserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.gameserver.api.dto.GameCreateRequest;
import com.example.gameserver.api.dto.PlayerJoinRequest;
import com.example.gameserver.entity.Game;
import com.example.gameserver.entity.Player;
import com.example.gameserver.enums.GameStatus;
import com.example.gameserver.enums.PlayerStatus;
import com.example.gameserver.exceptions.GameNotFoundException;
import com.example.gameserver.exceptions.HostAlreadyJoinedException;
import com.example.gameserver.exceptions.InvalidGameStateException;
import com.example.gameserver.repository.GameRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import jakarta.transaction.Transactional;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final ResourceService resourceService;
    private final BuildingService buildingService;
    private final GameRepository gameRepository;

    @Autowired
    public GameService(ResourceService resourceService, BuildingService buildingService, GameRepository gameRepository) {
        this.resourceService = resourceService;
        this.buildingService = buildingService;
        this.gameRepository = gameRepository;
    }

    // Create a new game with the given hostId and settings
    @Transactional
    public Game createGame(GameCreateRequest request) {

        if(request.getHostId() == null) {
            throw new IllegalArgumentException("Host id cannot be null");
        }
        if(request.getSettings() == null) {
            throw new IllegalArgumentException("Settings cannot be null");
        }

        Game game = new Game();
        game.setHostId(request.getHostId());

        Set<Player> players = new HashSet<>();
        Player player = new Player();
     
        player.setId(request.getHostId());
        player.setName("Host");
        player.setStatus(PlayerStatus.ACTIVE);
        players.add(player);
        game.setPlayers(players);
        game.setStatus(GameStatus.WAITING);
        game.setSettings(request.getSettings());

        logger.info("Game created with id: {}", game.getId());
        gameRepository.save(game);
        return game;
    }
    // Host is starting the game with the given gameId
    @Transactional
    public Game startGame(Long gameId) {
        if(gameId == null) {
            throw new IllegalArgumentException("Game id cannot be null");
        }
        Future<Game> futureGame = getGameById(gameId);
        try{
            Game game = futureGame.get();
            if (game.getStatus() != GameStatus.WAITING) {
                throw new InvalidGameStateException("Game already started");
            }
    
            game.setStatus(GameStatus.IN_PROGRESS); 
            gameRepository.save(game);
    
            
            
            return game;
        } catch (Exception e) {
            throw new GameNotFoundException(gameId);
        }

        
    }
    // Join the game with the given gameId and request details a different player than the host
   
    @Transactional
    public Game joinGame(Long gameId, PlayerJoinRequest request) {
        if(request.getPlayerId() == null) {
            throw new IllegalArgumentException("Player id cannot be null");
        }
        if(request.getPlayerName() == null) {
            throw new IllegalArgumentException("Player name cannot be null");
        }
        if(gameId == null) {
            throw new IllegalArgumentException("Game id cannot be null");
        }

        Future<Game> futureGame = getGameById(gameId);
        try{
            Game game = futureGame.get();
            Player player = new Player();
            if(game.getHostId().equals(request.getPlayerId())) {
                throw new HostAlreadyJoinedException(gameId);
            }
            player.setId(request.getPlayerId());
            player.setName(request.getPlayerName());
            player.setStatus(PlayerStatus.ACTIVE);
    
            game.getPlayers().add(player);
            gameRepository.save(game);
            return game;
        } catch (Exception e) {
            throw new GameNotFoundException(gameId);
        }
       
      
    }
    // Get the game with the given gameId
    @Async
    public Future<Game> getGameById(Long gameId) {
        if(gameId == null) {
            throw new IllegalArgumentException("Game id cannot be null");
        }
        return CompletableFuture.completedFuture(gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId)));
       
    }
    

    // List all games
    @Async

    public Future<Iterable<Game>> listGames() {
        //Find all available games 
        return CompletableFuture.completedFuture(gameRepository.findAll());
    }

}