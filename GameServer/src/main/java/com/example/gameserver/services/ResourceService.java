package com.example.gameserver.services;
import com.example.gameserver.api.dto.User.PlayerDetailsDTO;
import com.example.gameserver.entity.Game;
import com.example.gameserver.enums.GameStatus;
import com.example.gameserver.exceptions.GameNotFinishedException;
import com.example.gameserver.exceptions.GameNotFoundException;
import com.example.gameserver.repository.BuildingRepository;
import com.example.gameserver.repository.GameRepository;
import com.example.gameserver.repository.ResourceRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.gameserver.entity.Resources;
import com.example.gameserver.entity.User;
import com.example.gameserver.enums.ResourceType;


import java.util.Map;
import java.util.Optional;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class ResourceService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);
    private final GameRepository gameRepository;
    private final ResourceRepository resourceRepository;



    public ResourceService(GameRepository gameRepository, BuildingRepository buildingRepository, ResourceRepository resourceRepository) {
        this.gameRepository = gameRepository;
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public String initializePlayerResources(Long gameId) {
        if (gameId == null) {
            return null;
        }

        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + gameId);
            return null;
        }

        Set<User> players = game.get().getPlayers();
        if(players.isEmpty()) {
            logger.error("No players found for game, ID: " + gameId);
            return null;
        }

        
        for (User player : players) {
            Resources resources = new Resources(gameId, player.getId());
            resourceRepository.save(resources);
        }

        return "Resources initialized for all players"; 
    }

    //clears the resouces of the game from the DB after the game is declared finished
    @Transactional
    public void clearResources(Long gameId) {
        if (gameId == null) {
            return;
        }

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));

        if(game.getStatus() != GameStatus.FINISHED) {
            throw new GameNotFinishedException(gameId);
        }

        resourceRepository.deleteAll(resourceRepository.findAll().stream().filter(resources -> resources.getGameId().equals(gameId)).toList());
        resourceRepository.flush();
    }

    @Async
    public Future<Resources> getPlayerResources(Long gameId, Long playerId) {
        if (gameId == null || playerId == null) {
            return null;
        }
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + gameId);
            return null;
        }
        PlayerDetailsDTO player = game.get().getPlayerById(playerId);
        if (player == null) {
            logger.error("Player not found, ID: " + playerId);
            return null;
        }

        Optional<Resources> resources = resourceRepository.findByGameIdAndPlayerId(gameId, playerId);
        if (resources.isEmpty()) {
            logger.error("Resources not found, Game ID: " + gameId + ", Player ID: " + playerId);
            return null;
        }

        return CompletableFuture.completedFuture(resources.get());
    }

    @Transactional
    public Resources addResource(Long gameId, Long playerId, Map<ResourceType,Integer> resourcesQuantities) {
        if (gameId == null || playerId == null || resourcesQuantities == null) {
            return null;
        }
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + gameId);
            return null;
        }
        PlayerDetailsDTO player = game.get().getPlayerById(playerId);
        if (player == null) {
            logger.error("Player not found, ID: " + playerId);
            return null;
        }

        Optional<Resources> playerResources = resourceRepository.findByGameIdAndPlayerId(gameId, playerId);

        if (playerResources.isEmpty()) {
            logger.error("Resources not found, Game ID: " + gameId + ", Player ID: " + playerId);
            return null;
        }

        for (Map.Entry<ResourceType, Integer> entry : resourcesQuantities.entrySet()) {
            playerResources.get().add(entry.getKey(), entry.getValue());
        }
        resourceRepository.save(playerResources.get());

        return playerResources.get();

        
    }

    @Transactional
    public Resources removeResource(Long gameId, Long playerId, ResourceType resourceType, int amount) {
        if (gameId == null || playerId == null || resourceType == null || amount <= 0) {
            return null;
        }
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + gameId);
            return null;
        }
        PlayerDetailsDTO player = game.get().getPlayerById(playerId);
        if (player == null) {
            logger.error("Player not found, ID: " + playerId);
            return null;
        }

        Optional<Resources> resources = resourceRepository.findByGameIdAndPlayerId(gameId, playerId);
        if (resources.isEmpty()) {
            logger.error("Resources not found, Game ID: " + gameId + ", Player ID: " + playerId);
            return null;
        }

        Resources playerResources = resources.get();
        playerResources.subtract(resourceType, amount);
        resourceRepository.save(playerResources);
        return playerResources;
    }

   

}
