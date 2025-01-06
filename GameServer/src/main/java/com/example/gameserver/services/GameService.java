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
    private final GameRepository gameRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public GameService(GameRepository gameRepository, UsersRepository usersRepository) {
        this.gameRepository = gameRepository;
        this.usersRepository = usersRepository;
    }


    // Create a new game with the given hostId and settings
    @Transactional
    public Game createGame(GameCreateRequest request) {
        if (request.getName() == null) {
            throw new IllegalArgumentException("Game name cannot be null");
        }
        if (request.getMaxPlayers() < 2) {
            throw new IllegalArgumentException("Max Players Settings must be at least 2");
        }
        if (request.getHostName() == null) {
            throw new IllegalArgumentException("Host name cannot be null");
        }

        // Get or create the player
        User player = usersRepository.getUserByName(request.getHostName());
        if (player == null) {
            player = new User();
            player.setName(request.getHostName());
            player.setHost(true);
            player = usersRepository.save(player);  // Save and get the persisted entity
        }

        if (player.getGameId() != null) {
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
        if (gameId == null) {
            throw new IllegalArgumentException("Game id cannot be null");
        }
        Future<Game> futureGame = getGameById(gameId);
        try {
            Game game = futureGame.get();
            if (game.getStatus() != GameStatus.WAITING) {
                throw new InvalidGameStateException("Game already started");
            }

            game.setStatus(GameStatus.IN_PROGRESS);
            gameRepository.save(game);

            return game;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // End the game with the given gameId
    @Transactional
    public void endGame(Long gameId) {
        if (gameId == null) {
            throw new IllegalArgumentException("Game id cannot be null");
        }

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));

        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new InvalidGameStateException("Game not in progress!");
        }

        game.setStatus(GameStatus.FINISHED);
        gameRepository.save(game);
    }

    // Join the game with the given gameId and request details a different player than the host
    @Transactional
    public Game joinGame(Long gameId, PlayerJoinRequest request) {

        if (gameId == null) {
            throw new IllegalArgumentException("Game id cannot be null");
        }
        if (request.getPlayerName() == null) {
            throw new IllegalArgumentException("Player name cannot be null");
        }
        User player = usersRepository.getUserByName(request.getPlayerName());

        System.out.println("\n\nPlayer Name: " + request.getPlayerName() + " isRefresh: " + request.isRefresh());

        if (player == null) {
            System.out.println("\n\nPlayer is null");
            player = new User();
            player.setName(request.getPlayerName());
            player.setGameId(gameId);
            player.setHost(false);
            player = usersRepository.save(player);
        } else {
            System.out.println("\n\nPlayer is not null");
            if(!request.isRefresh()) {
                System.out.println("\n\nNot a refresh");
                return null;
            }
        }

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
        if (request.isRefresh()) {
            return game;
        }
        if (game.getStatus() != GameStatus.WAITING) {
            throw new InvalidGameStateException("Game already started");
        }
        if (game.getHostId().equals(player.getId())) {
            throw new HostAlreadyJoinedException(player.getId());
        }
        if (game.getPlayers().size() >= game.getSettings().getMaxPlayers()) {
            throw new InvalidGameStateException("Game is full");
        }
        if (player.getGameId() != null && !player.getGameId().equals(gameId)) {
            throw new InvalidGameStateException("Player already in a game");
        }

        System.out.println("adding player to game");
        game.getPlayers().add(player);
        System.out.println("player added to game");
        game.getSettings().setCurrentPlayersCount(game.getPlayers().size());
        gameRepository.save(game);
        System.out.println("game saved");
        return game;
    }


    @Transactional
    public void leaveGame(Long gameId, PlayerJoinRequest request) {

        if (gameId == null) {
            throw new IllegalArgumentException("Game id cannot be null");
        }
        if (request.getPlayerName() == null) {
            throw new IllegalArgumentException("Player name cannot be null");
        }

        User player = usersRepository.getUserByName(request.getPlayerName());

        if (player == null) {
            throw new IllegalArgumentException("Player cannot be found");
        }

        Game game = gameRepository.findGameById(gameId);
        if (game == null) {
            throw new GameNotFoundException(gameId);
        }

        if (game.getStatus() == GameStatus.FINISHED) {
            return;
        }

        game.getPlayers().remove(player);
        player.setGameId(null);
        game.getSettings().setCurrentPlayersCount(game.getPlayers().size());

        if (game.getPlayers().isEmpty()) { // if game is empty, delete it
            gameRepository.delete(game);
            usersRepository.delete(player);
            return;
        }

        if (player.isHost()) // if host leaves, assign a new host
        {
            player.setHost(false);
            User newHost = game.getPlayers().iterator().next();
            System.out.println("\n\nNew host id: " + newHost.getId());
            System.out.println("\n\nOld host id: " + player.getId());
            game.setHostId(newHost.getId());
            newHost.setHost(true);
            usersRepository.save(newHost);
        }
        gameRepository.save(game);
        usersRepository.delete(player);
    }

    // Get the game with the given gameId
    @Async
    public Future<Game> getGameById(Long gameId) {
        if (gameId == null) {
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