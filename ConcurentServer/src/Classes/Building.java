package Classes;

import Enums.ResourceType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Building {
    private String name;
    private Map<ResourceType, Integer> requiredResources;
    private Map<ResourceType, Integer> resourceProduction;

    public Building(String name, Map<ResourceType, Integer> required, Map<ResourceType, Integer> production) {
        this.name = name;
        this.requiredResources = Map.copyOf(required);
        this.resourceProduction = Map.copyOf(production);
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

