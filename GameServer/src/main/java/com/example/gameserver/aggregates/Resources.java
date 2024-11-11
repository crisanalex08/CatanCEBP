package com.example.gameserver.aggregates;

import lombok.Data;
import com.example.gameserver.enums.ResourceType;

import java.util.HashMap;


@Data
public class Resources {

    private HashMap<ResourceType, Integer> resources;

    

    
}
