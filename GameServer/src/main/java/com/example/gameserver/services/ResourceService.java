package com.example.gameserver.services;

import com.example.gameserver.entity.Game;
import com.example.gameserver.entity.Player;
import com.example.gameserver.repository.BuildingRepository;
import com.example.gameserver.repository.GameRepository;
import com.example.gameserver.repository.ResourceRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.gameserver.entity.Resources;
import com.example.gameserver.enums.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ResourceService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);
    private final GameRepository gameRepository;
    private final BuildingRepository buildingRepository;
    private final ResourceRepository resourceRepository;

    public ResourceService(GameRepository gameRepository, ResourceRepository resourceRepository, BuildingRepository buildingRepository) {
        this.gameRepository = gameRepository;
        this.resourceRepository = resourceRepository;
        this.buildingRepository = buildingRepository;
    }

    public Resources initializePlayerResources(Long gameId, Long playerId) {
        if (gameId == null || playerId == null) {
            return null;
        }
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + gameId);
            return null;
        }

        Player player = game.get().getPlayerById(playerId);
        if (player == null) {
            logger.error("Player not found, ID: " + playerId);
            return null;
        }
        Resources resources = new Resources(gameId, playerId);

        resourceRepository.save(resources);
        return resources;
    }

    public Resources getPlayerResources(Long gameId, Long playerId) {
        if (gameId == null || playerId == null) {
            return null;
        }
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + gameId);
            return null;
        }
        Player player = game.get().getPlayerById(playerId);
        if (player == null) {
            logger.error("Player not found, ID: " + playerId);
            return null;
        }

        Optional<Resources> resources = resourceRepository.findByGameIdAndPlayerId(gameId, playerId);
        if (resources.isEmpty()) {
            logger.error("Resources not found, Game ID: " + gameId + ", Player ID: " + playerId);
            return null;
        }

        return resources.get();
    }

    @Transactional
    public Resources addResource(Long gameId, Long playerId, ResourceType resourceType, int amount) {
        if (gameId == null || playerId == null || resourceType == null || amount <= 0) {
            return null;
        }
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + gameId);
            return null;
        }
        Player player = game.get().getPlayerById(playerId);
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
        playerResources.add(resourceType, amount);
        resourceRepository.save(playerResources);
        return playerResources;
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
        Player player = game.get().getPlayerById(playerId);
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

    public List<Resources> distributeResources(Long gameId, Long playerId) {
        if(gameId == null || playerId == null) {
            return null;
        }
//        int diceRoll = new Random().nextInt(6) + 1;

        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            logger.error("Game not found, ID: " + gameId);
            return null;
        }
        Player player = game.get().getPlayerById(playerId);
        if (player == null) {
            logger.error("Player not found, ID: " + playerId);
            return null;
        }

        List<Resources> modifiedResources = new ArrayList<>();
        addResource(gameId, playerId, ResourceType.WOOD, 1);
        addResource(gameId, playerId, ResourceType.CLAY, 1);
        removeResource(gameId, playerId, ResourceType.CLAY, 1);
        modifiedResources.add(addResource(gameId, playerId, ResourceType.WHEAT, 1));

        return modifiedResources;
    }

}
