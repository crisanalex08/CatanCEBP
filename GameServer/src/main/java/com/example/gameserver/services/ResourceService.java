package com.example.gameserver.services;

import org.springframework.stereotype.Service;

import com.example.gameserver.entity.Resources;
import com.example.gameserver.enums.ResourceType;

@Service
public class ResourceService {

    public Resources initializePlayerResources(String gameId, String playerId) {
        return null;
    }

    public Resources getPlayerResources(String gameId, String playerId) {
        return null;
    }
    
    public Resources addResource(String gameId, String playerId, ResourceType resourceType, int amount) {
        return null;
    }

    public Resources removeResource(String gameId, String playerId, ResourceType resourceType, int amount) {
        return null;
    }

    public Resources distributeResources(String gameId, String playerId) {
        return null;
    }

    
    
}
