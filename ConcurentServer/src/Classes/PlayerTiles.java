package Classes;

import Enums.ResourceType;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerTiles {
    private String playerId;
    private ArrayList<ResourceType> resources;

    public PlayerTiles(String playerId, ResourceType[] resources) {
        this.playerId = playerId;
        this.resources = new ArrayList<>(Arrays.asList(resources));
    }

    public String getPlayerId() {
        return playerId;
    }

    public ArrayList<ResourceType> getResources() {
        return resources;
    }
}
