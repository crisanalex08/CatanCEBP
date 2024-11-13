package com.example.gameserver.entity;

import lombok.Data;
import com.example.gameserver.enums.ResourceType;


import java.util.concurrent.ConcurrentHashMap;


@Data

public class Resources {



    
    private ConcurrentHashMap<ResourceType, Integer> resources;

  
    

    
}
