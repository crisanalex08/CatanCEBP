package Interfaces;

import java.util.Map;

import Enums.ResourceType;

public interface IBuilding {
    String getName();
    Map<ResourceType, Integer> getRequiredResources();
    Map<ResourceType, Integer> getResourceProduction();
}
