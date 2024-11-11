package com.example.gameserver.aggregates;

import lombok.Data;
import com.example.gameserver.enums.ResourceType;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.concurrent.ConcurrentHashMap;


@Data
@Schema(name = "Resources", description = "Resources of a player")
public class Resources {

    
    private ConcurrentHashMap<ResourceType, Integer> resources;

  
    

    
}
