package Classes;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Enums.ResourceType;
import Interfaces.IBuilding;

public class Settlement implements IBuilding {
    private String name;
    private Map<ResourceType, Integer> requiredResources;
    private Map<ResourceType, Integer> resourceProduction;
    private Map<Integer,ResourceType> diceRollResources;
    public Settlement() {
        this.name = "Settlement";

        initRequiredResources();

        Random random = new Random();
        int numResources = random.nextInt(3) + 1;
        Map<Integer,ResourceType> diceMap = new HashMap<>();
        int resourceEnumLength = ResourceType.values().length;

        for (int i = 0; i < numResources; i++) {
            ResourceType resource = ResourceType.values()[random.nextInt(resourceEnumLength)];
            diceMap.put(random.nextInt(11) + 2, resource);
        }


        this.diceRollResources = diceMap;

        System.out.println("Settlement with " + diceMap + " was created");
    }
    public Settlement(Boolean tryToBuild)
    {
        this.name = "Settlement";

        initRequiredResources();

        Random random = new Random();
        int numResources = random.nextInt(3) + 1;
        Map<Integer,ResourceType> diceMap = new HashMap<>();
        int resourceEnumLength = ResourceType.values().length;

        for (int i = 0; i < numResources; i++) {
            ResourceType resource = ResourceType.values()[random.nextInt(resourceEnumLength)];
            diceMap.put(random.nextInt(11) + 2, resource);
        }

        this.diceRollResources = diceMap;

        System.out.println("Settlement with " + diceMap + "is trying to be built");
    }


    private void initRequiredResources() {
        this.requiredResources = Map.of(
            ResourceType.WOOD, 1,
            ResourceType.CLAY, 1,
            ResourceType.WHEAT, 1,
            ResourceType.SHEEP, 1
        );
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
