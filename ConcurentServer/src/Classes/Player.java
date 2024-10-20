package Classes;

import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;
import Enums.ResourceType;
import Enums.ConstructionTypeEdge;
import Enums.ConstructionTypeVertex;

public class Player {
    String id;
    String name;
    private EnumMap<ResourceType, Integer> resources;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.resources = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
    }

    public void takeAction(GameState gameState) {
        // Simulate a player action
        int action = ThreadLocalRandom.current().nextInt(0, 5);
        switch (action) {
            case 0:
                gameState.rollDice(this);
                break;
            case 1:
                gameState.placeConstruction(this, ConstructionTypeVertex.SETTLEMENT, null, 0);
            
                break;
            case 2:
                gameState.initiateTrade(this);
                break;
            case 3:
                gameState.placeConstruction(this, ConstructionTypeVertex.CITY, null, 0);
                break;
            case 4:
                gameState.placeConstruction(this, null, ConstructionTypeEdge.ROAD, 0);
                break;
            default:
                break;
        }
    }

    // Methods for adding, removing, and querying resources
}
