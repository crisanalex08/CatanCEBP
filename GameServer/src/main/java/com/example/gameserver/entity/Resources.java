package com.example.gameserver.entity;

import lombok.Data;
import com.example.gameserver.enums.ResourceType;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Data
@Entity
public class Resources {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String gameId;
    private String playerId;
    private Map <ResourceType, Integer> resources = new ConcurrentHashMap<>();
    

    
}
