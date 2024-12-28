package com.example.gameserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.gameserver.api.dto.GameCreateRequest;
import com.example.gameserver.api.dto.PlayerJoinRequest;
import com.example.gameserver.entity.Game;
import com.example.gameserver.entity.GameSettings;
import com.example.gameserver.entity.User;
import com.example.gameserver.enums.GameStatus;
import com.example.gameserver.exceptions.GameNotFoundException;
import com.example.gameserver.exceptions.HostAlreadyJoinedException;
import com.example.gameserver.exceptions.InvalidGameStateException;
import com.example.gameserver.repository.GameRepository;
import com.example.gameserver.repository.UsersRepository;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import java.util.concurrent.Future;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final ResourceService resourceService;
    private final BuildingService buildingService;
    private final GameRepository gameRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public GameService(ResourceService resourceService, BuildingService buildingService, GameRepository gameRepository, UsersRepository usersRepository) {
        this.resourceService = resourceService;
        this.buildingService = buildingService;
        this.gameRepository = gameRepository;
        this.usersRepository = usersRepository;
    }

  
    // Create a new game with the given hostId and settings
    @Transactional
    public Game createGame(GameCreateRequest request) {
        if(request.getName() == null) {
            throw new IllegalArgumentException("Game name cannot be null");
        }
        if(request.getMaxPlayers() < 2) {
            throw new IllegalArgumentException("Max Players Settings must be at least 2");
        }
        if(request.getHostName() == null) {
            throw new IllegalArgumentException("Host name cannot be null");
        }
    
        // Get or create the player
        User player = usersRepository.getUserByName(request.getHostName());
        if (player == null) {
            player = new User();
            player.setName(request.getHostName());
            player = usersRepository.save(player);  // Save and get the persisted entity
        }
    
        if(player.getGameId() != null) {
            throw new InvalidGameStateException("Player already in a game");
        }
    
        // Create the game
        Game game = new Game();
        game.setHostId(player.getId());
        game.setName(request.getName());
        game.setStatus(GameStatus.WAITING);
        
        GameSettings settings = new GameSettings();
        settings.setMaxPlayers(request.getMaxPlayers());
        settings.setCurrentPlayersCount(1);
        game.setSettings(settings);
    
        
        Set<User> players = new HashSet<>();
        players.add(player);
        game.setPlayers(players);
    
        game = gameRepository.save(game);
        
        // Update player's game association
        player.setGameId(game.getId());
        usersRepository.save(player);
    
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
    
        if(gameId == null) {
            throw new IllegalArgumentException("Game id cannot be null");
        }
        if(request.getPlayerName() == null) {
            throw new IllegalArgumentException("Player name cannot be null");
        }
        

        User player = usersRepository.getUserByName(request.getPlayerName());
        

        if(player == null) {
            player = new User();
            player.setName(request.getPlayerName());
            usersRepository.save(player);
        }

        Future<Game> futureGame = getGameById(gameId);
        try{
            Game game = futureGame.get();
            if(player.getGameId() != null && player.getGameId().equals(gameId)) {
                return game;
            }
            if (game.getStatus() != GameStatus.WAITING) {
                throw new InvalidGameStateException("Game already started");
            }
            if(game.getHostId().equals(player.getId())) {
                throw new HostAlreadyJoinedException(player.getId());
            }
            if(game.getPlayers().size() >= game.getSettings().getMaxPlayers()) {
                throw new InvalidGameStateException("Game is full");
            }
            if(player.getGameId() != null && !player.getGameId().equals(gameId)) {
                throw new InvalidGameStateException("Player already in a game");
            }
            game.getPlayers().add(player);
            game.getSettings().setCurrentPlayersCount(game.getPlayers().size());
            gameRepository.save(game);
            
            player.setGameId(game.getId());
            usersRepository.save(player);

            return game;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }

    @Transactional
    public void leaveGame(Long gameId, PlayerJoinRequest request) {

        if(gameId == null) {
            throw new IllegalArgumentException("Game id cannot be null");
        }
        if(request.getPlayerName() == null) {
            throw new IllegalArgumentException("Player name cannot be null");
        }

        User player = usersRepository.getUserByName(request.getPlayerName());

        if(player == null) {
            throw new IllegalArgumentException("Player cannot be found");
        }

        Game game = gameRepository.findGameById(gameId);
        if(game == null) {
            throw new GameNotFoundException(gameId);
        }

        if(game.getStatus() == GameStatus.FINISHED){
            return;
        }

        game.getPlayers().remove(player);
        game.getSettings().setCurrentPlayersCount(game.getPlayers().size());

        player.setGameId(null);
        if(game.getPlayers().isEmpty()) {
            gameRepository.delete(game);
            return;
        }
        gameRepository.save(game);
        usersRepository.save(player);

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