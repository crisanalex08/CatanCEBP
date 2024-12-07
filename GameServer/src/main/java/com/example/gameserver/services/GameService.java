package com.example.gameserver.services;

import org.springframework.beans.factory.annotation.Autowired;
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
            throw new IllegalArgumentException("Host id cannot be null");
        }
        if(request.getMaxPlayers() == 0) {
            throw new IllegalArgumentException("Max Players Settings must be higher than 1");
        }

        var player = usersRepository.getUserByUsername(request.getHostname());
        if (player == null) {
            //Create a new user
            player = new User();
            player.setUsername(request.getHostname());
       
            usersRepository.save(player);
        }

        // if(player.getGames() !=null){
        //     player.getGames().forEach(game -> {
        //         if(game.getStatus() == GameStatus.WAITING){
        //             throw new InvalidGameStateException("Player already in a game");
        //         }
        //     });
        // }
        if(player.getGameId() !=null){
            throw new InvalidGameStateException("Player already in a game");
    
        }
        Game game = new Game();
        game.setHostId(player.getId());
        game.setName(request.getName());
        game.setStatus(GameStatus.WAITING);
        GameSettings settings = new GameSettings();
        settings.setMaxPlayers(request.getMaxPlayers());
        settings.setCurrentPlayersCount(1);
        game.setSettings (settings);
         
        User host = usersRepository.findById(player.getId().toString()).get();

        
        game.setPlayers(new HashSet<>(){{
            add(host);
        }});


        logger.info("Game created with id: {}", game.getId());
        gameRepository.save(game);
        host.setGameId(game.getId());
        usersRepository.save(host);
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
        if(request.getPlayerId() == null) {
            throw new IllegalArgumentException("Player id cannot be null");
        }
        User player = usersRepository.findById(request.getPlayerId().toString()).orElse(null);

        if(player == null) {
            throw new IllegalArgumentException("Player does not exist");
        }

        Future<Game> futureGame = getGameById(gameId);
        try{
            Game game = futureGame.get();
            if (game.getStatus() != GameStatus.WAITING) {
                throw new InvalidGameStateException("Game already started");
            }
            if(game.getHostId().equals(request.getPlayerId())) {
                throw new HostAlreadyJoinedException(request.getPlayerId());
            }
            if(game.getPlayers().size() >= game.getSettings().getMaxPlayers()) {
                throw new InvalidGameStateException("Game is full");
            }
            if(player.getGameId() != null) {
                throw new InvalidGameStateException("Player already in a game");
            }
            game.getPlayers().add(player);
            game.getSettings().setCurrentPlayersCount(game.getSettings().getCurrentPlayersCount() + 1);
            gameRepository.save(game);
            
            player.setGameId(game.getId());
            usersRepository.save(player);

            return game;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
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