package Interfaces;

import java.util.Map;

import Enums.ResourceType;

public interface Building {
    String getName();
    Map<ResourceType, Integer> getRequiredResources();
    Map<ResourceType, Integer> getResourceProduction();
}
