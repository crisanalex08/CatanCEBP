package Classes;

import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;
import Enums.ResourceType;
import Enums.ActionsType;
import Enums.ConstructionTypeEdge;
import Enums.ConstructionTypeVertex;

public class Player {
    String id;
    String name;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
     
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }


    public void takeAction(GameState gameState, ActionsType action) {
        // Simulate a player action
       
        switch (action) {
            case ROLL_DICE:
                gameState.rollDice(this);
                break;
            case PLACE_SETTLEMENT:
                gameState.placeConstruction(this, ConstructionTypeVertex.SETTLEMENT, null, 0);
            
                break;
            case INITIATE_TRADE:
                gameState.initiateTrade(this);
                break;
            case PLACE_CITY:
                gameState.placeConstruction(this, ConstructionTypeVertex.CITY, null, 0);
                break;
            case    PLACE_ROAD:
                gameState.placeConstruction(this, null, ConstructionTypeEdge.ROAD, 0);
                break;
            case ACCEPT_TRADE:
                gameState.acceptTrade(this);
                break;
            case DECLINE_TRADE:
                gameState.declineTrade(this);
                break;

            default:
                break;
        }
    }


    // Methods for adding, removing, and querying resources
}
