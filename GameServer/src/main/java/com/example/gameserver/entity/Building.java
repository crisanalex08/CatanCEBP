package com.example.gameserver.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


//import com.example.gameserver.enums.BuildingStatus;
import com.example.gameserver.enums.BuildingType;
import com.example.gameserver.enums.ResourceType;
import com.example.gameserver.models.ProductionData;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Entity
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private BuildingType type;
    private Long playerId;
    private Long gameId;
   
    @ElementCollection(fetch = FetchType.EAGER)
    private List<ProductionData> production;
    
    

    public Building() {
    }

    public Building(Long gameId, Long playerId, BuildingType type) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.type = type;
        this.production = new ArrayList<>();
        initializeProductionData();
        

    }

    public void initializeProductionData()
    {
        List<ResourceType> possibleResources = new ArrayList<>();
        if (this.type == BuildingType.SETTLEMENT) {
            
            possibleResources = Arrays.asList(
                ResourceType.WOOD, 
                ResourceType.CLAY, 
                ResourceType.STONE
            );
        }
        else if (this.type == BuildingType.TOWN) {

            possibleResources = Arrays.asList(
                ResourceType.WHEAT, 
                ResourceType.SHEEP, 
                ResourceType.GOLD
            );
        }

        if(possibleResources.size() < 2) {
            throw new IllegalStateException("Not enough resources for building type: " + type);
        }


            Collections.shuffle(possibleResources);
            List<ResourceType> resources = possibleResources.subList(0, 2);
            
            Random random = new Random();
            Set<Integer> diceValues = new HashSet<>();

            for(ResourceType resource : resources) {
                ProductionData data = new ProductionData();
                data.setResourceType(resource);
                data.setProductionRate(1);
                int diceValue;
                do {
                    diceValue = random.nextInt(6) + 1;
                } while (diceValues.contains(diceValue));

                diceValues.add(diceValue);
                data.setDiceValue(diceValue);
                this.production.add(data);
            }
        
    }
    

    public Map<ResourceType, @Min(0) Integer> produce() {
        Map<ResourceType, @Min(0) Integer> resources = new ConcurrentHashMap<>();

        for (ProductionData data : production) {
            resources.put(data.getResourceType(), data.getProductionRate());
        }

        return resources;
    }

      


}
