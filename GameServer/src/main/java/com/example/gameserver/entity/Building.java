package com.example.gameserver.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


//import com.example.gameserver.enums.BuildingStatus;
import com.example.gameserver.enums.BuildingType;
import com.example.gameserver.enums.ResourceType;
import com.example.gameserver.models.ProductionData;


import jakarta.persistence.*;
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
    @ElementCollection
    private List<ProductionData> production = new ArrayList<>();

    public Building() {
    }

    public Building(Long gameId, Long playerId, BuildingType type) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.type = type;
    }

    public Map<ResourceType, @Min(0) Integer> produce() {
        Map<ResourceType, @Min(0) Integer> resources = new ConcurrentHashMap<>();

        switch (type) {
            case SETTLEMENT:
                resources.put(ResourceType.WOOD, 1);
                resources.put(ResourceType.CLAY, 1);
                resources.put(ResourceType.WHEAT, 1);
                break;
            case TOWN:
                resources.put(ResourceType.STONE, 1);
                resources.put(ResourceType.SHEEP, 1);
                resources.put(ResourceType.GOLD, 1);
                break;
            default:
                break;
        }

        return resources;
    }


}
