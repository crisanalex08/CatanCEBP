package com.example.gameserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.gameserver.api.dto.GameCreateRequest;
import com.example.gameserver.api.dto.PlayerJoinRequest;
import com.example.gameserver.entity.Game;
import com.example.gameserver.entity.User;
import com.example.gameserver.enums.GameStatus;
import com.example.gameserver.exceptions.GameNotFoundException;
import com.example.gameserver.exceptions.HostAlreadyJoinedException;
import com.example.gameserver.exceptions.InvalidGameStateException;
import com.example.gameserver.repository.GameRepository;
import com.example.gameserver.repository.UsersRepository;

import java.util.HashSet;
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

        if(request.getHostId() == null) {
            throw new IllegalArgumentException("Host id cannot be null");
        }
        if(request.getSettings() == null) {
            throw new IllegalArgumentException("Settings cannot be null");
        }
        if(usersRepository.findById(request.getHostId().toString()).isEmpty()) {
            throw new IllegalArgumentException("Host does not exist");
        }


        Game game = new Game();
        game.setHostId(request.getHostId());
        game.setStatus(GameStatus.WAITING);
        game.setSettings(request.getSettings());

         
        User host = usersRepository.findById(request.getHostId().toString()).get();

        game.setPlayers(new HashSet<>(){{
            add(host);
        }});


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
            if(game.getHostId().equals(request.getPlayerId())) {
                throw new HostAlreadyJoinedException(gameId);
            }
            game.getPlayers().forEach(p -> {
                if(p.getId().equals(request.getPlayerId())) {
                    throw new IllegalArgumentException("Player already joined");
                }
            });

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