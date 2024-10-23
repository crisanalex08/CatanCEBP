package Classes;

import Enums.ResourceType;

import java.util.HashMap;

public class Resources {
    private HashMap<ResourceType,Integer> resources;

    public Resources() {
        this.resources = new HashMap<ResourceType,Integer>();
        for (ResourceType resourceType : ResourceType.values()) {
            this.resources.put(resourceType, 0);
        }
    }

    public Resources(HashMap<ResourceType,Integer> resources) {
        this.resources = resources;
    }

    public void addResource(ResourceType resourceType, int quantity) {
        this.resources.put(resourceType, this.resources.get(resourceType) + quantity);
    }

    public void removeResource(ResourceType resourceType, int quantity) {
        this.resources.put(resourceType, this.resources.get(resourceType) - quantity);
    }

    public HashMap<ResourceType,Integer> getResources() {
        return this.resources;
    }

    public ResourceType getMinResource() {
        ResourceType minResource = null;
        int minQuantity = Integer.MAX_VALUE;
        for (ResourceType resourceType : ResourceType.values()) {
            if (this.resources.get(resourceType) < minQuantity) {
                minResource = resourceType;
                minQuantity = this.resources.get(resourceType);
            }
        }
        return minResource;
    }

    public ResourceType getMaxResource() {
        ResourceType maxResource = null;
        int maxQuantity = Integer.MIN_VALUE;
        for (ResourceType resourceType : ResourceType.values()) {
            if (this.resources.get(resourceType) > maxQuantity) {
                maxResource = resourceType;
                maxQuantity = this.resources.get(resourceType);
            }
        }
        return maxResource;
    }

}
