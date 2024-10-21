package Classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Enums.ActionsType;

public class Player implements Runnable {
    String id;
    String name;
    ArrayList<ActionsType> actionsToTake;
    GameState gameState;


    public Player(String id, String name, ArrayList<ActionsType> actionsToTake, GameState gameState) {
        this.id = id;
        this.name = name;
        if (gameState == null){
            throw new IllegalArgumentException("gameState cannot be null");
        }
        this.gameState = gameState;
        if(actionsToTake != null) {
            this.actionsToTake = actionsToTake;
        } else {
            if(this.id.equals("1")) {
                this.actionsToTake = new ArrayList<>(Arrays.asList(ActionsType.ROLL_DICE, ActionsType.INITIATE_TRADE));
            } else {
                this.actionsToTake = new ArrayList<>(List.of(ActionsType.ROLL_DICE));
            }
        }

    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    @Override
    public void run() {
            gameState.takeAction(this);
    }

}

