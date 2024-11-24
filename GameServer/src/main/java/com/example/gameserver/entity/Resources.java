package com.example.gameserver.entity;

import com.example.gameserver.enums.BuildingType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.annotations.Cascade;

import com.example.gameserver.enums.ResourceType;

import jakarta.validation.constraints.Min;


@Data
@Entity
public class Resources {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long gameId;
    private Long playerId;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<ResourceType, @Min(0) Integer> quantities = new ConcurrentHashMap<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Resources() {
    }

    public Resources(Long gameId, Long playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
        quantities.put(ResourceType.WOOD, 0);
        quantities.put(ResourceType.CLAY, 0);
        quantities.put(ResourceType.WHEAT, 0);
        quantities.put(ResourceType.STONE, 0);
        quantities.put(ResourceType.SHEEP, 0);
        quantities.put(ResourceType.GOLD, 0);
    }

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
        quantities.replace(type, quantities.getOrDefault(type, 0) + amount);
    }

    public synchronized void subtract(ResourceType type, int amount) {
        Integer current = quantities.getOrDefault(type, 0);
        if (current < amount) {
            throw new IllegalArgumentException("Not enough resources");
        }
        quantities.replace(type, current - amount);
    }

    public boolean hasEnoughResources(ResourceType type, int amount) {
        return quantities.getOrDefault(type, 0) >= amount;
    }

    public boolean hasEnoughResources(BuildingType type) {
        return switch (type) {
            case SETTLEMENT ->
                    hasEnoughResources(ResourceType.WOOD, 1) && hasEnoughResources(ResourceType.CLAY, 1) && hasEnoughResources(ResourceType.WHEAT, 1);
            case TOWN ->
                    hasEnoughResources(ResourceType.WOOD, 2) && hasEnoughResources(ResourceType.CLAY, 2) && hasEnoughResources(ResourceType.WHEAT, 2);
            case CASTLE ->
                    hasEnoughResources(ResourceType.WOOD, 3) && hasEnoughResources(ResourceType.STONE, 3) && hasEnoughResources(ResourceType.SHEEP, 3) && hasEnoughResources(ResourceType.GOLD, 3);
        };
    }

}
