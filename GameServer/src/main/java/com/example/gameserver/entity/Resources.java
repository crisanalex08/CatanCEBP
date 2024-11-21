package com.example.gameserver.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.gameserver.enums.ResourceType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Min;


@Data

public class Resources {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String gameId;
    private String playerId;

    private Map<ResourceType, @Min(0) Integer> quantities = new ConcurrentHashMap<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public synchronized void add(ResourceType type, int amount) {
        quantities.put(type, quantities.getOrDefault(type, 0) + amount);
    }

    public synchronized void subtract(ResourceType type, int amount) {
        quantities.put(type, quantities.getOrDefault(type, 0) - amount);
    }

    public boolean hasEnoughResources(ResourceType type, int amount) {
        return quantities.getOrDefault(type, 0) >= amount;
    }

    


    


}
