package Classes;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Enums.ResourceType;
import Interfaces.Building;

public class Settlement implements Building {
    private String name;
    private Map<ResourceType, Integer> requiredResources;
    private Map<ResourceType, Integer> resourceProduction;
    private Map<Integer,ResourceType> diceRollResources;
    public Settlement() {
        this.name = "Settlement";
        this.requiredResources = Map.of(
            ResourceType.WOOD, 1,
            ResourceType.CLAY, 1,
            ResourceType.WHEAT, 1,
            ResourceType.SHEEP, 1
        );
        Random random = new Random();
        int numResources = random.nextInt(3) + 1;

        Map<Integer,ResourceType> diceMap = new HashMap<>();
        
        int resourceEnumLength = ResourceType.values().length;

        for (int i = 0; i < numResources; i++) {
            ResourceType resource = ResourceType.values()[random.nextInt(resourceEnumLength)];
            diceMap.put(random.nextInt(11) + 2, resource);
        }


        this.diceRollResources = diceMap;
    


    }
    public Map<Integer, ResourceType> getDiceRollResources() {
        return diceRollResources;
    }

    public Map<ResourceType, Integer> getRequiredResources() {
        return requiredResources;
    }

    public Map<ResourceType, Integer> getResourceProduction() {
        
        return resourceProduction;
    }

    public String getName() {
        return name;
    }

    
}
