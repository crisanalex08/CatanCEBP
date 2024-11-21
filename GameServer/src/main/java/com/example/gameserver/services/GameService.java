package com.example.gameserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gameserver.api.dto.GameCreateRequest;
import com.example.gameserver.api.dto.PlayerJoinRequest;
import com.example.gameserver.entity.Game;
import com.example.gameserver.entity.Player;
import com.example.gameserver.enums.PlayerStatus;
import com.example.gameserver.exceptions.GameNotFoundException;
import com.example.gameserver.exceptions.InvalidGameStateException;
import com.example.gameserver.repository.GameRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();
    private final ResourceService resourceService;
    private final BuildingService buildingService;
    private final GameRepository gameRepository;

    @Autowired
    public GameService(ResourceService resourceService, BuildingService buildingService, GameRepository gameRepository) {
        this.resourceService = resourceService;
        this.buildingService = buildingService;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public Game createGame(GameCreateRequest request) {
        Game game = new Game();
        game.setHostId(request.getHostId());

        Set<Player> players = new HashSet<>();
        Player player = new Player();
        player.setId(request.getHostId());
        player.setName("Host");
        player.setStatus(PlayerStatus.ACTIVE);
        players.add(player);
        game.setPlayers(players);

        game.setSettings(request.getSettings());

        logger.info("Game created with id: {}", game.getId());
        gameRepository.save(game);
        return game;
    }

    public Game startGame(String gameId) {
        Game game = getGameById(gameId);
        if (game == null) {
            throw new GameNotFoundException(gameId);
        }

        Game gameState = activeGames.get(gameId);
        if (gameState == null) {
            throw new InvalidGameStateException("Game state not initialized");
        }
        return game;
    }

    public Game getGameById(String gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
    }

    @Transactional
    public Game joinGame(String gameId, PlayerJoinRequest request) {
        Game game = getGameById(gameId);
        Player player = new Player();
        player.setId(request.getPlayerId());
        player.setName(request.getPlayerName());
        player.setStatus(PlayerStatus.ACTIVE);

        game.getPlayers().add(player);
        gameRepository.save(game);
        return game;
    }
}